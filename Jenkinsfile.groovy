/**
 * This pipeline will execute a simple maven build, using a Persistent Volume Claim to store the local Maven repository
 *
 * A PersistentVolumeClaim needs to be created ahead of time with the definition in maven-with-cache-pvc.yml
 *
 * NOTE that typically writable volumes can only be attached to one Pod at a time, so you can't execute
 * two concurrent jobs with this pipeline. Or change readOnly: true after the first run
 */

podTemplate(
        name: 'maven-builder',
        namespace: 'jenkins',
        containers: [
                containerTemplate(name: 'git', image: 'alpine/git', ttyEnabled: true, command: 'cat'),
                containerTemplate(name: 'maven', image: 'maven:3.6.3-jdk-11-slim', ttyEnabled: true, command: 'cat')
        ], volumes: [
        persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repo', readOnly: false)]
) {
    environment {
        HELM_RELEASE_NAME_ENV = ""
        HELM_NAMESPACE_ENV = ""
    }
    properties([
            parameters([
                    gitParameter(branch: '',
                            branchFilter: 'origin/(.*)',
                            defaultValue: 'master',
                            description: '',
                            name: 'BRANCH',
                            quickFilterEnabled: false,
                            selectedValue: 'NONE',
                            sortMode: 'NONE',
                            tagFilter: '*',
                            type: 'PT_BRANCH')
            ])
    ])
    node(POD_LABEL) {
        stage('Git checkout') {
            container('git') {
                sh "git clone -b ${params.BRANCH} https://github.com/DENMROOT/billing.git"
            }
        }
        stage('Build Maven project') {
            container('maven') {
                dir('billing/') {
                    sh 'mvn -B clean install'
                }
            }
        }
        stage('Build and push docker image') {
            container('maven') {
                dir('billing/') {
                    withCredentials([
                            string(credentialsId: 'aws_access_key_id', variable: 'AWS_ACCESS_KEY'),
                            string(credentialsId: 'aws_secret_access_key', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                        sh 'apt-get update && apt-get install -y amazon-ecr-credential-helper'
                        sh 'mvn package com.google.cloud.tools:jib-maven-plugin:1.3.0:build -DskipTests'
                    }

                }
            }
        }
        stage('Deploy to K8s') {
            container('maven') {
                dir('billing/') {
                    withEnv([
                            "HELM_RELEASE_NAME_ENV=billing-app-${params.BRANCH}",
                            "HELM_NAMESPACE_ENV=billing-application-${params.BRANCH}"]
                    ) {
                        echo "RELEASE = ${env.HELM_RELEASE_NAME_ENV}"
                        echo "NAMESPACE = ${env.HELM_NAMESPACE_ENV}"

                        sh 'apt-get update && apt-get install -y snapd'
                        sh 'systemctl start snapd.service'

                        sh 'snap install helm --classic'
                        sh 'snap install skaffold'

                        sh 'skaffold run'
                    }
                }
            }
        }
    }
}

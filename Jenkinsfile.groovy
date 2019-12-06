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
                containerTemplate(name: 'maven', image: 'maven:3.6.3-jdk-11-slim', ttyEnabled: true, command: 'cat')
        ], volumes: [
        persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repo', readOnly: false)]
) {
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
        stage('Build a Maven project') {
            container('maven') {
                sh 'apt-get update && apt-get install -y git'
                sh "git clone https://github.com/DENMROOT/billing.git"
                sh "git checkout ${params.BRANCH}"
                sh 'mvn -B clean compile'
            }
        }
        stage('Test Maven project') {
            container('maven') {
                sh 'mvn -B test'
            }
        }
        stage('Build and push docker image') {
            container('maven') {
                sh 'apt-get update && apt-get install -y amazon-ecr-credential-helper'
                sh 'mvn compile com.google.cloud.tools:jib-maven-plugin:1.3.0:build'
            }
        }
    }
}

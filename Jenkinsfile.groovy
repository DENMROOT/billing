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
        stage('Deploy to K8s') {
            container('maven') {
                dir('billing/') {
                    withEnv([
                            "HELM_RELEASE_NAME_ENV=billing-app-${params.BRANCH}",
                            "HELM_NAMESPACE_ENV=billing-application-${params.BRANCH}"]
                    ) {
                        withCredentials([
                                string(credentialsId: 'aws_access_key_id', variable: 'AWS_ACCESS_KEY'),
                                string(credentialsId: 'aws_secret_access_key', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                            echo "RELEASE = ${env.HELM_RELEASE_NAME_ENV}"
                            echo "NAMESPACE = ${env.HELM_NAMESPACE_ENV}"

                            sh 'apt-get update && apt-get install -y amazon-ecr-credential-helper curl'

                            sh 'curl -Lo skaffold https://storage.googleapis.com/skaffold/releases/latest/skaffold-linux-amd64'
                            sh 'chmod +x skaffold'
                            sh 'mv skaffold /usr/local/bin'

                            sh 'curl https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 > get_helm.sh'
                            sh 'chmod 700 get_helm.sh'
                            sh './get_helm.sh'

                            sh 'mkdir ~/.docker'
                            sh 'cat > ~/.docker/config.json << ENDOFFILE\n' +
                                    '{\n' +
                                    '    "credsStore": "ecr-login"\n' +
                                    '}\n' +
                                    'ENDOFFILE'

                            sh "helm ls"
                            sh "helm install --name billing-app ./billing-app/"
                        }
                    }
                }
            }
        }
    }
}

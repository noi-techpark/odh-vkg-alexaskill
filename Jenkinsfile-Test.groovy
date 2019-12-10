pipeline {
    agent {
        dockerfile {
            filename 'docker/dockerfile-node'
            additionalBuildArgs '--build-arg JENKINS_USER_ID=`id -u jenkins` --build-arg JENKINS_GROUP_ID=`id -g jenkins`'
        }
    }

    environment {
        ASK_REFRESH_TOKEN = credentials('odh-vkg-alexaskill-ask-refresh-token')
        ASK_VENDOR_ID = credentials('odh-vkg-alexaskill-ask-vendor-id')
        SKILL_ID = "amzn1.ask.skill.15666bcd-6da0-4f2b-a6e8-ee1cb3f17058"
        SKILL_NAME = "odh-vkg"
    }

    stages {
        stage('Setup') {
            steps {
                sh 'git config --global user.email "info@opendatahub.bz.it"'
                sh 'git config --global user.name "Jenkins"'
            }
        }
        stage('Clone') {
            steps {
                sh 'ask clone --skill-id ${SKILL_ID}'
            }
        }
        stage('Copy & Configure') {
            steps {
                sh 'cp -R lambda ${SKILL_NAME}/lambda'
                sh 'cp -R models ${SKILL_NAME}/models'
            }
        }
        stage('Deploy') {
            steps {
                sh 'cd ${SKILL_NAME} && git add -A'
                sh 'cd ${SKILL_NAME} && git commit -m "Deployment"'
                sh 'cd ${SKILL_NAME} && ask deploy'
            }
        }
    }
}

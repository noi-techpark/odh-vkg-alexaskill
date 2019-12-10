pipeline {
    agent {
        dockerfile {
            filename 'docker/dockerfile-node'
            additionalBuildArgs '--build-arg JENKINS_USER_ID=`id -u jenkins` --build-arg JENKINS_GROUP_ID=`id -g jenkins`'
        }
    }

    environment {
        ASK_CLI_CONFIG = credentials('odh-vkg-alexaskill-cli-config')
        SKILL_ID = "amzn1.ask.skill.15666bcd-6da0-4f2b-a6e8-ee1cb3f17058"
        SKILL_NAME = "odh-vkg"
        GIT_NAME = "Jenkins"
        GIT_EMAIL = "info@opendatahub.bz.it"
    }

    stages {
        stage('Configure') {
            steps {
                sh 'mkdir -p ~/.ask'
                sh 'cat "${ASK_CLI_CONFIG}" > ~/.ask/cli_config'
                sh 'git config --global user.email "${GIT_EMAIL}"'
                sh 'git config --global user.name "${GIT_NAME}"'
            }
        }
        stage('Clone') {
            steps {
                sh 'ask clone --skill-id ${SKILL_ID}'
            }
        }
        stage('Copy') {
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

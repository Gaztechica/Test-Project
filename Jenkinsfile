pipeline {
   agent any
   tools {
    maven 'maven-3.9'
}

  environment {
        ENV = 'staging'
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('API Tests') {
            parallel {
                stage('API Regression') {
                    steps {
                        sh 'mvn test -Papi-regression || true'
                    }
                }
                stage('API Smoke') {
                    steps {
                        sh 'mvn test -Papi-smoke || true'
                    }
                }
            }
        }

        stage('UI Tests') {
//         agent {
//                 docker { image 'markhobson/maven-chrome:jdk-21' }
//             }
            steps {
//                 sh 'mvn test -Pui || true'
                sh 'mvn test -Pui -Dselenide.headless=true || true'
            }
        }

        stage('Integration Tests') {
            steps {
                sh 'docker-compose up -d'
                sh 'mvn test -Pintegration || true'
            }
            post {
                always {
                    sh 'docker-compose down'
                }
            }
        }
    }

    post {
        always {
            allure includeProperties: false,
                   jdk: '',
                   properties: [],
                   reportBuildPolicy: 'ALWAYS',
                   results: [[path: 'target/allure-results']]

            script {
                if (currentBuild.result == 'FAILURE' || currentBuild.currentResult == 'FAILURE') {
                    slackSend color: 'danger', message: "❌ Pipeline failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
                } else {
                    slackSend color: 'good', message: "✅ All tests passed: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
                }
            }
        }
    }
}

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
//                 docker {
//                 image 'markhobson/maven-chrome:jdk-21'
//                 args '-v /var/run/docker.sock:/var/run/docker.sock'
//               }
//             }
            steps {
                sh 'mvn test -Pui || true'
//                 sh 'mvn test -Pui -Dselenide.headless=true || true'
            }
        }

        stage('Integration Tests') {
            steps {
                sh 'docker-compose up -d'
                sh 'mvn test -Pintegration || true'
            }
            post {
                always {
                    sh 'docker-compose down || true'
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
                       results: [[path: 'target/allure-results']],
                       markUnstable: false

                script {
                    echo "Сборка полностью завершена. Результаты прогона агрегированы Allure."
                }
            }
        }
    }

//             script {
//                             if (currentBuild.result == 'FAILURE' || currentBuild.currentResult == 'FAILURE') {
//                                 echo "❌ Pipeline status: FAILURE"
//                             } else {
//                                 echo "✅ Pipeline status: SUCCESS / ALL TESTS PASSED"
//                             }
//             }
//         }

}

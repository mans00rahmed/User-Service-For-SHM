pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/mans00rahmed/User-Service-For-SHM.git'
            }
        }

        stage('Build & Test') {
            steps {
                bat 'mvnw.cmd clean verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'target/site/jacoco/jacoco.xml']],
                        id: 'user-service-coverage',
                        name: 'User Service Coverage'
                    )
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    bat 'mvnw.cmd sonar:sonar -Dsonar.projectKey=user-service -Dsonar.projectName="User Service"'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        always {
            echo 'User Service pipeline finished.'
        }
        success {
            echo 'User Service built successfully.'
        }
        failure {
            echo 'User Service pipeline failed.'
        }
    }
}
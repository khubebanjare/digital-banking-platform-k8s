pipeline {
    agent any

    tools {
        jdk 'jdk21'
    }

    environment {
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './gradlew :auth-service:clean :auth-service:build'
            }
        }

        stage('JaCoCo Coverage') {
            steps {
                sh './gradlew :auth-service:jacocoTestReport'
            }
        }

        stage('Mutation Testing') {
            steps {
                sh './gradlew :auth-service:pitest'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh './gradlew :auth-service:sonar'
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                    testResults: '**/build/test-results/test/*.xml'

            publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'auth-service/build/reports/jacoco/test/html',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Coverage Report'
            ])
            echo "==================================="
            echo "QUALITY REPORTS"
            echo "==================================="

            echo "JaCoCo Report:"
            echo "${env.BUILD_URL}artifact/auth-service/build/reports/jacoco/test/html/index.html"

            echo "PIT Mutation Report:"
            echo "${env.BUILD_URL}artifact/auth-service/build/reports/pitest/index.html"

            echo "SonarQube Dashboard:"
            echo "http://localhost:9000/dashboard?id=auth-service"
        }

        success {
            echo 'Auth Service Build Successful'
        }

        failure {
            echo 'Auth Service Build Failed'
        }
    }

}

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

                sh '''
            echo "==================================="
            echo " JaCoCo Report Generated"
            echo "==================================="
            echo "Report:"
            echo "${WORKSPACE}/auth-service/build/reports/jacoco/test/html/index.html"
        '''
            }
        }

        stage('Mutation Testing') {
            steps {
                sh './gradlew :auth-service:pitest'

                sh '''
            echo "==================================="
            echo " PIT Mutation Report Generated"
            echo "==================================="
            echo "Report:"
            echo "${WORKSPACE}/auth-service/build/reports/pitest/index.html"
        '''
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh './gradlew :auth-service:sonar'
                }

                sh '''
            echo "==================================="
            echo " SonarQube Analysis Completed"
            echo "==================================="
            echo "Dashboard:"
            echo "http://localhost:9000/dashboard?id=auth-service"
        '''
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
        }

        success {
            echo 'Auth Service Build Successful'

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

        failure {
            echo 'Auth Service Build Failed'

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
    }

}

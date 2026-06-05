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
        }

        success {
            echo 'Auth Service Build Successful'
        }

        failure {
            echo 'Auth Service Build Failed'
        }
    }

}

pipeline {
    agent any

    tools {
        jdk 'jdk21'
    }

    environment {
        GRADLE_OPTS = '-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs="-Xmx2048m -XX:MaxMetaspaceSize=512m"'
        JAVA_HOME = '/opt/jdk-21.0.11'
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        retry(2)
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                retry(3) {
                    timeout(time: 10, unit: 'MINUTES') {
                        sh './gradlew :auth-service:clean :auth-service:build --no-daemon --stacktrace'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                retry(2) {
                    timeout(time: 15, unit: 'MINUTES') {
                        sh './gradlew :auth-service:test --no-daemon --stacktrace'
                    }
                }
            }
        }

        stage('JaCoCo Coverage') {
            steps {
                retry(2) {
                    timeout(time: 10, unit: 'MINUTES') {
                        sh './gradlew :auth-service:jacocoTestReport --no-daemon'
                    }
                }
            }
        }

        stage('Mutation Testing') {
            steps {
                retry(2) {
                    timeout(time: 15, unit: 'MINUTES') {
                        sh './gradlew :auth-service:pitest --no-daemon'
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                retry(2) {
                    timeout(time: 10, unit: 'MINUTES') {
                        withSonarQubeEnv('SonarQube') {
                            sh './gradlew :auth-service:sonar --no-daemon'
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                    testResults: '**/build/test-results/test/*.xml'

            cleanWs()

            archiveArtifacts(
                    artifacts: 'auth-service/build/reports/jacoco/test/html/**',
                    allowEmptyArchive: true
            )

            archiveArtifacts(
                    artifacts: 'auth-service/build/reports/pitest/**',
                    allowEmptyArchive: true
            )

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

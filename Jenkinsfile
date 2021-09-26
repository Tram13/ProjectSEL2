pipeline {
  agent any
  environment {
        LIVETESTS = '1'
  }
  stages {
    stage('Test stage') {
      parallel {
        stage('Frontend test') {
          when {
            anyOf {
              branch 'master'
              branch 'frontend'
              not {
                branch pattern: '.*back.*', comparator: 'REGEXP'
              }

            }

          }
          steps {
            nvm(version: 'v14.15.5') {
              sh '''cd frontend
npm install --no-save
npm run test'''
            }

          }
        }

        stage('Backend test') {
          when {
            anyOf {
              branch 'master'
              branch 'backend'
              not {
                branch pattern: '.*front.*', comparator: 'REGEXP'
              }

            }

          }
          steps {
            sh '''cd backend/api/api/
chmod +x mvnw
./mvnw clean test'''
          }
        }

      }
    }

    stage('Sonarqube stage') {
      parallel {
        stage('Sonarqube backend analysis') {
          when {
            anyOf {
              branch 'master'
              branch 'backend'
              not {
                branch pattern: '.*front.*', comparator: 'REGEXP'
              }

            }

          }
          steps {
            withSonarQubeEnv('sonarqube') {
              sh '''cd ./backend/api/api/
chmod +x   ./mvnw
./mvnw clean verify sonar:sonar'''
            }

          }
        }

        stage('Sonarqube frontend analysis') {
          when {
            anyOf {
              branch 'master'
              branch 'frontend'
              not {
                branch pattern: '.*back.*', comparator: 'REGEXP'
              }

            }

          }
          steps {
            nvm(version: 'v14.15.5') {
              sh '''cd ./frontend/
npm install -g sonarqube-scanner --no-save
npm run coverage
sonar-scanner -Dsonar.login=c1b0757eea55804e6e4c58b7ee6b9089cb343f8d'''
            }

          }
        }

      }
    }

    stage('Deploy stage') {
      parallel {
            stage('Backend deploy (/api/dev/)') {
          when {
            branch 'backend'
          }
          steps {
            dir(path: '/selabrepo-back-dev/backend/api/api/') {
              sh '''git checkout backend
git pull'''
              sh '''chmod +x ./mvnw
./mvnw clean package && sudo /usr/bin/systemctl restart api-dev.service
'''
            }

          }
    }
        
        stage('Frontend deploy (/app/dev/)') {
          when {
            branch 'frontend'
          }
          steps {
            dir(path: '/selabrepo-front-dev/frontend/') {
              nvm(version: 'v14.15.5') {
                sh '''git checkout frontend
git pull
npm install --no-save
npm run build'''
              }

            }

          }
        }      
        
        stage('Master deploy (/app, /api)') {
          when {
            branch 'master'
          }
          steps {
            dir(path: '/selabrepo/backup/') {
              sh '''git checkout master 
git pull'''
            }

            dir(path: '/selabrepo/frontend/') {
              nvm(version: 'v14.15.5') {
                sh '''npm install --no-save
npm run build'''
              }

            }

            dir(path: '/selabrepo/backend/api/api') {
              sh '''chmod +x ./mvnw
./mvnw clean package && sudo /usr/bin/systemctl restart api.service'''
            }

          }
        }

      }
    }
    
       stage('Databank reset') {
          when {
            branch 'backend'
          }
          steps {
            dir(path: '/selabrepo-back-dev/database/src/code/') {
              sh '''chmod +x reset_db.sh
sudo /usr/bin/systemctl start db_reset.service
'''
            }

          }
        }

    stage('cleanup') {
      steps {
        cleanWs(cleanWhenAborted: true, cleanWhenFailure: true, cleanWhenNotBuilt: true, cleanWhenSuccess: true, cleanWhenUnstable: true, deleteDirs: true)
      }
    }

  }
  options {
    parallelsAlwaysFailFast()
  }
}

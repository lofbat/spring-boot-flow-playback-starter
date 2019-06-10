mvn clean install -Dmaven.test.skip=true
cd target
mvn install:install-file -Dfile=spring-boot-flow-playback-starter-0.0.1-SNAPSHOT.jar.original -Dpackaging=jar -DgroupId=io.github.lofbat -DartifactId=spring-boot-flow-playback-starter -Dversion=0.0.1-SNAPSHOT

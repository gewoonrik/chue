./gradlew bootRepackage
rsync build/libs/chue-0.2.54.jar gadgetlab.chnet:/srv/chue
ssh gadgetlab.chnet supervisorctl restart chue

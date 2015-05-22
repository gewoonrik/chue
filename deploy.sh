set -ex
./gradlew bootRepackage
rsync build/libs/chue-0.2.54.jar gadgetlab.chnet:/opt/chue
ssh gadgetlab.chnet sudo systemctl restart chue

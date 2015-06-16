CHue [![Build Status](https://travis-ci.org/WISVCH/chue.svg?branch=master)](https://travis-ci.org/WISVCH/chue)
====

Deploys to https://gadgetlab.chnet/chue/.

Deploying: `./deploy.sh`  
Log files: `ssh gadgetlab.chnet sudo journalctl -u chue.service`

## Development
Create new endpoints in `Webcontroller` by implementing either `HueState` or `HueEvent` and loading it using `HueService`. Events are temporary effects after which the lights will revert to the last known state.

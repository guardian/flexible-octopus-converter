# flexible-octopus-converter

This repo contains the lambda function which converts incoming Octopus JSON into Thrift. The lambda gets records from one Kinesis stream, validates and parses them, them puts them on a second Kinesis stream, which is made available to Editorial Tools services.

## Deployment

The lambda is deployed via RiffRaff. On merge to master, the lambda will be automatically deployed.

## Logs

Logs are sent to Cloudwatch and the lambda's performance can be monitored in [Grafana](https://metrics.gutools.co.uk/d/gLzfI4ZGz/octopus-overview).

## Links

The cloud formation for these resources can be found in the [editorial-tools-platform](https://github.com/guardian/editorial-tools-platform/blob/7e464297501f165f3981cf4391303b385ec9d54b/cloudformation/composer-account/octopus-integration.yaml) repo.

The thrift model can be found in the [flexible-octopus-model](https://github.com/guardian/flexible-octopus-model) repo.
# Settlement Data Pipeline Loader Template

This is a template repository for creating new SDP loaders.

To locally log into AWS CodeArtifact from command line:
`okta-aws-cli --profile mgmt --org-domain checkout.okta.com --oidc-client-id 0oar3nsvk7VtIvsL3357 --aws-acct-fed-app-id 0oa423kknpZCS07GJ357 -b -z --aws-iam-idp arn:aws:iam::791259062566:saml-provider/okta --aws-iam-role arn:aws:iam::791259062566:role/cko_issuing_engineer`

## Publishing Library

The build artifact is published to the CKO CodeArtifact repository.

### From main branch
1. Update the version in the build.gradle file
2. Push changes to the main branch
3. Create a tag with a convention of `X.Y.Z` where `X` is the major version, `Y` is the minor version, and `Z` is the patch version.
4. Push the tag to the repository

### From a feature branch
1. Create a PR
2. Add ``rc-publish`` label to the PR
3. Update the version in the build.gradle file accordingly (e.g. `X.Y.Z-rc1`)
4. Each push to the PR branch will trigger a release
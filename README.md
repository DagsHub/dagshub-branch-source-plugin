<div align="center">
  <a href="https://dagshub.com"><img src="https://raw.githubusercontent.com/DAGsHub/client/master/dagshub_github.png" width=600 alt=""/></a><br><br>
</div>

[![Discord](https://img.shields.io/discord/698874030052212737)](https://discord.com/invite/9gU36Y6)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Build](https://github.com/dagshub/dagshub-branch-source-plugin/actions/workflows/build-and-upload-artifact.yml/badge.svg)
<a href="https://twitter.com/TheRealDAGsHub" title="DAGsHub on Twitter"><img src="https://img.shields.io/twitter/follow/TheRealDAGsHub.svg?style=social"></a>

# DAGsHub Branch Source Jenkins Plugin

Use <a href="https://dagshub.com">DAGsHub</a> repositories as sources for
<a href="https://www.jenkins.io/doc/book/pipeline/multibranch/">Jenkins Multibranch Workflow projects</a>.

After installing the plugin, when you create or edit a Jenkins multibranch project, you can select a 
DAGsHub repo as a branch source, which means you can trigger automatic builds for any git branch, tag 
or pull request in your repo, if they have an existing 
[Jenkinsfile](jenkins.io/doc/book/pipeline/jenkinsfile/).

## Installation
As of now, this plugin is not yet listed in the official Jenkins plugin repository (we're working on it!).

So, to install it right now, you can: 
1. Go to the [latest release](https://github.com/dagshub/dagshub-branch-source-plugin/releases).
1. Download the `dagshub-branch-source.hpi` file from the release assets.
1. Go to your Jenkins' advanced plugin management screen:  
   https://your-jenkins-instance/pluginManager/advanced 
1. Choose the "Upload Plugin" option, upload the hpi file.

## Release notes
[See the Github releases](https://github.com/dagshub/dagshub-branch-source-plugin/releases) for release notes.

---

Made with 🐶 by [DAGsHub](https://dagshub.com/).

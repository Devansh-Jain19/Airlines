# Maven Start Up PowerShell script for Windows
$ErrorActionPreference = "Stop"
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$wrapperJar = Join-Path $scriptDir ".mvn\wrapper\maven-wrapper.jar"

& java "-Dmaven.multiModuleProjectDirectory=$scriptDir" -cp $wrapperJar org.apache.maven.wrapper.MavenWrapperMain @args

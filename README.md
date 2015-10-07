# sbt-bower: SBT Bower Plugin

[Simple Build Tool] plugin for using [bower] to manage frontend dependencies.

[Simple Build Tool]: http://simple-build-tool.googlecode.com
[bower]: http://bower.io/

# NOTE

This library is no longer maintained, since the [Webjars](http://www.webjars.org/) [bower](http://www.webjars.org/bower) project
provides a far superior method of using bower dependencies for Scala projects. Would highly recommend checking it out/converting.

# Installation
First make sure you have [bower] installed, do this by using [node.js] [npm]. The artifacts are hosted on Sonatype, so
you shouldn't need to add any repositories

Then simply place this in your `project/build.sbt` file

```scala
addSbtPlugin("org.mdedetrich" %% "sbt-bower" % "0.2.1")
```

In your `build.sbt` file, put

```scala
seq(bowerSettings : _*)
```

Now we need to simply setup our dependencies in `build.sbt` which follow a similar format to then `bower.json` format.
Here is an example of such a configuration

```scala
BowerKeys.frontendDependencies ++= Seq(
  "angular" %%% "=1.2.0-rc.2",
  "angular-scenario" %%% "=1.2.0-rc.2",
  "angular-route" %%% "=1.2.0-rc.2",
  "angular-mocks" %%% "=1.2.0-rc.2",
  "angular-animate" %%% "=1.2.0-rc.2",
  "angular-cookies" %%% "=1.2.0-rc.2",
  "angular-resource" %%% "=1.2.0-rc.2",
  "angular-sanitize" %%% "=1.2.0-rc.2",
  "angular-touch" %%% "=1.2.0-rc.2",
  "requirejs" %%% "=2.1.8",
  "requirejs-text" %%% "2.0.10"
)
```

Note that since we use the official [bower] binary, the versioning will follow the exact same semantics
documented on the [bower] page.

We can also specify hook scripts to run like this:

hookScripts in Bower ++= Seq(
  PostInstall := "bower-requirejs -c ./requirejs.config.js"
)

Valid script types are (PreInstall, PostInstall, PreUnInstall)


The last thing we need to do is to setup the [bower] source directories and installation directory.
By default the source directory is `sourceDirectory (_ / "main" / "webapp" )` which implies you
have a project which uses the [xbst-web-plugin]. The installation directory defaults to
`(sourceDirectory in Bower) (_ / "js" / "lib")`. These directories can be changed in build.sbt by doing
the following

```scala
sourceDirectory <<= sourceDirectory (_ / "main" / "assets" )
installDirectory <<=  (sourceDirectory in Bower) (_ / "js" / "myStuffGoesHere")
```

That's it, we now have now setup the plugin!

# Automatically check dependencies

We can set up SBT to automatically check our frontend dependencies when SBT starts in the
exact same way it checks for our scala/java dependencies. To do this, we just modify the
update task. Place the following in your `build.sbt`

```scala
update <<= update dependsOn (installTask dependsOn(pruneTask))
```

This will run the `bower:prune` and `bower:install` tasks when SBT starts which checks if you
currently have your frontend dependencies installed and update them should they have changed.
It will also cleanup any removed dependencies

Note that the source directory is just the directory where this plugin will execute [bower],
it just needs to exist and its often just the generic "assets" folder for your web application
(which if you are using [xbst-web-plugin] happens to be `sourceDirectory (_ / "main" / "webapp" )`)

[node.js]: http://nodejs.org/
[npm]: https://npmjs.org/
[bower]: http://bower.io/
[xbst-web-plugin]: https://github.com/JamesEarlDouglas/xsbt-web-plugin

## Notes

sbt-bower uses the PATH environment variable to locate the [bower] binary. If you run SBT from a
shell/command prompt it should work fine (assuming [node]/[npm] and [bower] are properly set up), however
if you happen to be using the [IntelliJ SBT Plugin], due to this [bug] then you need to resort to
using a proper terminal

[Intellij SBT Plugin]: http://plugins.jetbrains.com/plugin/5007
[bug]: https://github.com/orfjackal/idea-sbt-plugin/issues/83

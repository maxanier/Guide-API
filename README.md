# Guide-API - Village and Pillage [![](https://cf.way2muchnoise.eu/versions/380771.svg)](https://www.curseforge.com/minecraft/mc-mods/guide-api-village-and-pillage)

**WIP branch for 1.21.11+ - Everything may still change**

Library mod for easy creation of guide books.

Fork by maxanier of Guide-API for Minecraft 1.14+ (Village and Pillage)  
Original mod by TehNut and Tombenpotter. https://github.com/TeamAmeriFrance/Guide-API

Allows easy creation of a guide book for your mod.
Books are mostly maintained by Guide-API (it registers them, it puts them in its own creative tab, etc).
Starting with 1.21.11+, you have to provide the respective model files, but can use Guide-API's methods during
data-generation.

The guide book is created mostly in code whereas e.g. Patchouli is mostly JSON based. This means:
- Add or change content based on the configuration of your mod.
- Wrap lines and pages based automatically, so localized strings don't overflow
- Refer to the set keybindings
- Refer to internal constants, balancing values etc. so they are automatically changed in the book if you change them in
  the mod
- Add custom page types, recipe types, etc.
- Use helper methods to generate parts of the book automatically

What else?
- No hard dependency, if Guide-API is not present at runtime the book won't be there, but everything else works
- Use TextFormatting and manual \n
- Several ready-to-use page types like text, item/block focused text, recipe, and image pages 


## Useful Links
* [CurseForge](https://www.curseforge.com/minecraft/mc-mods/guide-api-village-and-pillage)
* [Modrinth](https://modrinth.com/mod/guide-api)


## Mods that make use of Guide-API
The ones we know of at least

* [Vampirism](https://www.curseforge.com/minecraft/mc-mods/vampirism-become-a-vampire)


## Developer Information
If you need any assistance adding your own guide book, or if you are missing a feature, create an issue here.

### Structure

- `api`: This should be mostly relevant for you. Note: This does not only contain "classical" API methods, but also
  plenty of implementation that you may want to utilize or extend
- `core`: The "mod" functionality of this mod. You probably should not (need to) use this
- `test`: Test books that are stripped from the shipped jar. You can use this as reference

### Setup
#### Setup Gradle build script
You should be able to include it with the following in your `build.gradle`:
```gradle
repositories {
    //Maven repo for Guide-API
    maven {
        url = "https://maven.maxanier.de/releases"
    }
}
dependencies {
        compileOnly "de.maxanier.guideapi:Guide-API-VP:${project.guideapi_version}"
        runtimeOnly "de.maxanier.guideapi:Guide-API-VP:${project.guideapi_version}"

}
```

#### Choose a version

For a list of available GuideAPI version,
see [CurseForge](https://www.curseforge.com/minecraft/mc-mods/guide-api-village-and-pillage) or
the [maven listing](https://maven.maxanier.de/#/releases/de/maxanier/guideapi/Guide-API-VP).

These properties can be set in a file named `gradle.properties`, placed in the same directory as your `build.gradle` file.
Example `gradle.properties`:
```
guideapi_version=1.21.1-2.3.0
```

#### Rerun Gradle setup commands

Please run the commands that you used to set up your development environment again.
E.g. `gradlew` or `gradlew --refresh-dependencies`

### How to create your book

Checkout the test books in the `test` source package.

Checkout Vampirism which adds an extensive guide
book [here](https://github.com/TeamLapen/Vampirism/blob/dev/projects/vampirism/src/integrations/guide/java/de/teamlapen/vampirism/common/integration/guide/GuideBook.java)

#### Model

To render the book in game, you will have to generate a model for it.

In the `GatherDataEvent.Client` you can generate models for your guidebooks using the `util/ModelHelper` methods (see
`test/TestModDataProvider`) or create the models otherwise.
Keep in mind that at least the item state file must be in the `guideapi_vp/items` assets folder.

#### Crafting recipe
Add a crafting recipe for your book like this
```
{
  "result": {
    "item": "guideapi_vp:vampirism-guidebook"
  },
  "ingredients": [
    {
      "item": "vampirism:vampire_fang"
    },
    {
      "item": "minecraft:book"
    }
  ],
  "conditions": [
    {
      "type": "forge:mod_loaded",
      "modid": "guideapi_vp"
    }
  ],
  "type": "minecraft:crafting_shapeless"
}
```
#### API stability

Binary breaking changes in the `.api` package are only introduced with new main versions `*.0.0-beta.1` or new MC
versions.
New features are introduced with major versions `*.*.0` (possibly with alpha and beta stages) and bugfixes are introduced with minor versions (without alpha and beta phase).


## Modpack Permissions
For full details, view our license. Distributor rights are automatically given to any user who wishes to include the mod in their modpack if the intent is not malicious and/or commercial. We reserve the right to change this section as needed.

# SkidBounce
A free hacked-client for Minecraft 1.8.9 Forge.

## Completion

skidding:
* [LiquidBounce](https://github.com/CCBlueX/LiquidBounce/tree/legacy)
* [FDPClient](https://github.com/SkidderMC/FDPClient)
* [NightX](https://github.com/Aspw-w/NightX-Client)
* [CrossSine](https://github.com/shxp3/CrossSine)
* [LiquidBouncePlus-Reborn](https://github.com/liquidbounceplusreborn/LiquidbouncePlus-Reborn)

As Of Latest Commit:\
:green_circle: 100% :yellow_circle: 66%\
:orange_circle: 33% :red_circle: 0%
|      Thing       |   Completion    |
|:----------------:|:---------------:|
|      NoSlow      | :yellow_circle: |
|      NoWeb       | :green_circle:  |
|      Speed       | :orange_circle: |
|     LongJump     |  :red_circle:   |
|       Fly        | :orange_circle: |
|      NoFall      | :yellow_circle: |
|       Hud        |  :red_circle:   |
|    Animations    |  :red_circle:   |
|      Jesus       |  :red_circle:   |
|     Disabler     | :orange_circle: |
|      Phase       |  :red_circle:   |
|     Velocity     |  :red_circle:   |
|     ClickGUI     |  :red_circle:   |
|    AutoBlock     |  :red_circle:   |
|     Modules      |  :red_circle:   |
|     Scaffold     |  :red_circle:   |
|      Tower       |  :red_circle:   |
|       Step       |  :red_circle:   |
|    Criticals     |  :red_circle:   |
|     AntiVoid     |  :red_circle:   |
|    FastClimb     |  :red_circle:   |
| InventoryManager |  :red_circle:   |
|      Spider      | :green_circle:  |
|     FastUse      |  :red_circle:   |
| Everything Else  |  :red_circle:   |

## Issues
If you notice any bugs or missing features, you can let us know by opening an issue [here](https://github.com/ManInMyVan/SkidBounce/issues).

## License
This project is subject to the [GNU General Public License v3.0](LICENSE). This does only apply for source code located directly in this clean repository. During the development and compilation process, additional source code may be used to which we have obtained no rights. Such code is not covered by the GPL license.

For those who are unfamiliar with the license, here is a summary of its main points. **This is by no means legal advice nor legally binding**.

You are allowed to
- use
- share
- modify

this project entirely or partially for free and even commercially. However, please consider the following:

- **You must disclose the source code of your modified work and the source code you took from this project. This means you are not allowed to use code from this project (even partially) in a closed-source (or even obfuscated) application.**
- **Your modified application must also be licensed under the GPL** 

Do the above and share your source code with everyone; just like we do.

## Setting up a Workspace
LiquidBounce is using Gradle, so make sure that it is installed properly. Instructions can be found on [Gradle's website](https://gradle.org/install/).
1. Clone the repository using `git clone https://github.com/ManInMyVan/SkidBounce/`. 
2. CD into the local repository folder.
3. Depending on which IDE you are using execute either of the following commands:
    - For IntelliJ: `gradlew setupDevWorkspace idea genIntellijRuns build`
    - For Eclipse: `gradlew setupDevWorkspace eclipse build`
4. Open the folder as a Gradle project in your IDE.
5. Select either the Forge or Vanilla run configuration.

## Additional libraries
### Mixins
Mixins can be used to modify classes at runtime before they are loaded. SkidBounce is using it to inject its code into the Minecraft client. This way, we do not have to ship Mojang's copyrighted code. If you want to learn more about it, check out its [Documentation](https://docs.spongepowered.org/5.1.0/en/plugin/internals/mixins.html).

## Contributing

We appreciate contributions. So if you want to support us, feel free to make changes to SkidBounce's source code and submit a pull request. Currently, our main goals are the following:
1. Improve SkidBounce's performance.
2. Re-work most of the render code.

If you have experience in one or more of these fields, we would highly appreciate your support.

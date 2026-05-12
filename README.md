# java-template-gen


this is BAD, but it was made while i was in math and watching videos
## Features
When implementing interfaces and extending abstract classes it will find and insert all their abstract functions.
If an extended class has a non default constructor, it will automatically super it with some default values.
Variables with the `Default` parameter automatically get accessors, mutators, inserted into the non default
constructor parameters and calls their respective mutators. With the `-m` arg it adds a main file and updates
your makefile with the given param to run the generated file. 

## Non-features
Does not support sub-directories, protected, final, native, strict, synchronized, transient or volatile

## Command & Arguments
#### Notes:
* Anything with `...` can be repeated.
* Do not add spaces for parameters (e.g. `Full Name`).
* If you dont follow the formats things will probably maybe almost definitely break.

| Arg | Parameters | Description |
|-----|------------|-------------|
| -c  | [class_name] | Name of the Java class. |
| -e  | [extends_class] | Class that you extend from |
| -v  | [type] [name] [modifiers] `...` | Variables |
| -i  | [interface] `...` | Interfaces that you implement. |
| -a  | true | Should the class be abstract does nothing if it's anything but true. |
| -m  | [make_command] | The command you use to run this Java class also creates a main file. |

Modifiers are the only ones with set values:
| Modifier | Value | Description |
|----------|-------|-------------|
| Default  | d     | If you have this it adds accessors, mutators and places definitions inside of the NON default constructor params as well as calls their respective mutators. |
| Final    | f     | This makes the variable final and adds a default value, see [Final Note](#final-note). |
| Static   | s     | This makes the variable static. |

Of course you should not use `Default` with `Final` or `Static` for obvious reasons.

##### Final Note:
If you use final with a class it will attempt to instantiate the class with an empty parameter, however as you
will probably see, this wont work for everything. I am not in charge of this however since there can be several
different constructors and maybe you want to do something wonky.

#### Examples
##### General

<sub> This is the Test class example. </sub>
```
java Template -c Thing -e BaseThing -v String var1 d int var2 d int MAX f Scanner input _ -i Serializable Area -m run
```

##### Variables
-v int MAX f
```
private final int MAX = 0;
```

-v Scanner input f
```
private final Scanner input = new Scanner();
```

-v String CLASS fs
```
private static final String CLASS = "";
```


TODO: makefile support

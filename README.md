# SimpleSorter Plugin

## Links
Main Github Link: [https://github.com/KingCreator11/SimpleSorter](https://github.com/KingCreator11/SimpleSorter)

## License
This plugin is open sourced using the Apache 2 License.

```
   Copyright 2021 KingCreator11

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

## Simple Description
Is your server lagging badly due to excessive amounts of hoppers used in massive storage systems? This plugin will be your savior then! This plugin can be used as a simple and less laggy alternative to redstone sorters and doesn't need to update a million hoppers every tick! or any hoppers at all!

> If you have any general suggestions for the plugin please add the suggestion tag and create a new issue!

## A lag perspective -> Redstone Sorters vs Simple Sorter
> TODO - Will add screenshots of tps using very large redstone sorters which a large server is likely to have compared to many simple sorters.

## How the Plugin Works
All sorters are stored within an sqlite file, deleting this file will cause all player sorters to be deleted and remove functionality. Whenever an item is inputted into an input chest, rather than doing what minecraft does by default which is checking the chests every tick using hoppers, this plugin simply listens to an event listener and waits for 2 seconds then sorts all the items in the chests very quickly. This approach means that there will be no lag when the sorter isn't in use. Since the items are not sorted immediately but quickly instead, even if a player attempts to sort a large amount of items at once the amount of lag will be minimal.

> If you have any suggestions for other lag improvements please add them as an issue and add a lag-improvement tag!

## Commands
All commands within the plugin are listed below along with the permission required to use them. Anywhere where `/ss` is listed, you can also use `/simplesorter`.

| Command | Permission | Description |
|---------|------------|-------------|
| `/ss help` | `simplesorter.usage` | The help command for the plugin, lists the usage for every command along with a small description |
| `/ss create <name>` | `simplesorter.usage` and the number of sorters the player currently has must be below `simplesorter.count.n` n in the permission. | Creates a new simple sorter named `<name>`, the name must be unique for the player. If the player has no `simplesorter.count.n` permission then it will be assumed the player can have infinite sorters. |
| `/ss delete <name>` | `simplesorter.usage` | Deletes a simple sorter named `<name>`. |
| `/ss delete <ign>:<name>` | `simplesorter.admin` | Deletes a simple sorter named `<name>` for player named `<ign>` |
| `/ss list` | `simplesorter.usage` | Lists the simple sorters you own |
| `/ss list <ign>` | `simplesorter.admin` | Lists the simple sorters for a player named `<ign>` |
| `/ss setinput <name>` | `simplesorter.usage` | Sets the chest pointed to as an input chest. Placing items into this chest will cause the items to be sorted out. There can be multiple input chests for a sorter. |
| `/ss removeinput` | `simplesorter.usage` | Removes the input chest pointed to from the sorter. |
| `/ss setinvalid <name>` | `simplesorter.usage` | Sets the invalid item output chest. If any items are placed which cannot be sorted into any of the chests, they'll be placed into this chest. If there is no invalid items chest for a sorter/the chest is filled then the items will stay in the input chest. There can be multiple invalid items chest and when one is filled the next will be used. |
| `/ss removeinvalid` | `simplesorter.usage` | Removes the invalid item output chest from the sorter. |
| `/ss setshulkerinput <name>` | `simplesorter.shulkers` and `simplesorter.usage` | Creates an autopacker for the shulkers in the sorted chests. Will always try adding items to the last unfilled shulker in the sorter chest or will add a new empty shulker if the system cannot find any. There can be multiple shulker input chests and the system will check all for empty shulkers. |
| `/ss removeshulkerinput` | `simplesorter.usage` | Removes a shulker input and causes the storage to revert to a normal item based storage rather than a shulker based one |
| `/ss sort <name>` | `simplesorter.usage` | Turns the selected chest into a sorter of the held item. This plugin can also sort certain nonstackables and other items. |
| `/ss removesorter` | `simplesorter.usage` | Removes the given chest from the sorter. |

## Config
This plugin has no configuration file as I was unable to think of anything worth configuring. If you feel as though there should be something configurable please add it as an issue marked as a suggestion.
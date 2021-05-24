# CustomShop

A Minecraft plugin that implements custom shop designs as a complement to
chest-shops.

## Type of Shops

1. Vending Machines (The only type being implemented right now)
2. Newt's Briefcase
3. Salesman's Deal

### Vending Machines

A shop design with 27 available slots used only for selling items. The shop is
capable of selling different types of items at once, each with its own price.

#### Steps to buy items

1.  Right click on the vending machine.
2.  Select the item to purchase.
3.  Type the amount of item to purchase in chat.

#### Steps to list items

1.  Sneak + punch the vending machine that you own with **empty main hand**.
2.  Put items into the shop, leaving one sample in main hand.
3.  Close inventory view and sneak + punch the vending machine again with
    the **item in main hand**.
4.  Type the price that you want to sell it for in chat.

### Newt's Briefcase

A shop capable of holding virtually as much items of the same type as you want
(the real limit being Java's Integer.MAX_VALUE, 2 147 483 647). This shop can be
used for both selling and buying at the same time.

### Salesman's Deal

A shop capable of selling items at an offer of a discounted price if the
purchase exceeds a certain quantity (e.g. 1 for $10, 10 for $90).

## Models

### Vending Machine

<table style="margin: auto; text-align: center; max-width: 100%;">
<tbody><tr>
<td scope="col" style="width: 68px;">
<a href="https://imgur.com/9O1uP3E" title="Wooden Vending Machine">
<img src="https://i.imgur.com/9O1uP3E.png" decoding="async" width="128"></a>
</td>
<td scope="col" style="width:68px">
<a href="https://imgur.com/hCeiTmn" title="Stone Vending Machine">
<img src="https://i.imgur.com/hCeiTmn.png" decoding="async" width="128"></a>
</td>
<td scope="col" style="width:68px">
<a href="https://imgur.com/SyNNdEH" title="Nether Vending Machine">
<img src="https://i.imgur.com/SyNNdEH.png" decoding="async" width="128"></a>
</td>
<td scope="col" style="width:68px">
<a href="https://imgur.com/L9KKCZD" title="Sand Vending Machine">
<img src="https://i.imgur.com/L9KKCZD.png" decoding="async" width="128"></a>
</td></tr>
<tr>
<td>Wooden Vending Machine</td>
<td>Stone Vending Machine</td>
<td>Nether Vending Machine</td>
<td>Sand Vending Machine</td>
</tr>
<tr>
<td scope="col" style="width: 68px;">
<a href="https://imgur.com/1Jj8Gc6" title="Prismarine Vending Machine">
<img src="https://i.imgur.com/1Jj8Gc6.png" decoding="async" width="128"></a>
</td></tr>
<tr>
<td>Prismarine Vending Machine</td>
</tr>
</tbody></table>

## Commands

The plugin provides the following commands:

-   `/customshop newshop` - Opens up a shop creation GUI with available designs
    for players to choose from.
-   `/customshop removeshop` - An alternative way of removing shops that you own
    other than breaking the shop manually.
-   `/customshop gettotal` - Get the total number of shops that you owned.
-   `/customshop setcrate` - Set a block as a crate to use the custom shop crate
    key on.
-   `/customshop lockall <player>` - Locks all the available designs of a player.
-   `/customshop givekey <player> <amount>` - Give player the specified amount
    of crate keys.

## Dependencies

The plugin requires **ProtocolLib**, **Vault** and one of its supporting economy
plugins to be enabled.

## Installation

1.  Download the latest version of the plugin .jar file and put it into the
    `plugins` folder.
2.  Download the resource pack needed to render the custom shop's textures here,
    and combine it with your server resource pack or put it in your personal
    resource pack folder.
3.  Start/restart the server.

## Deconflicts

There are certain data that this plugin uses that may conflict with other
plugins or the serverpack.

### Custom Model IDs

The following IDs for `Material.PAPER` are used for custom shops. Change
**BOTH** the conflicting IDs in the items' JSON and the respective IDs in
`config.yml`.

| **ID** | **Model**               |
| ------ | ----------------------- |
| 100000 | default_vending_machine |
| 100001 | wooden_vending_machine  |
| 100002 | stone_vending_machine   |
| 100003 | nether_vending_machine  |
| 100004 | sand_vending_machine    |

### Display Names

The following display names are used for items used in GUIs and armor stands.
Blacklist these names so that players are not able to rename their items to the
following to abuse the plugin. Also check if these display names affect other
plugins.

| **Material**             | **Display Name**      |
| ------------------------ | --------------------- |
| ARMOR_STAND              | `§5§lVending Machine` |
| ARROW                    | `§eNext`              |
|                          | `§eBack`              |
| BARRIER                  | `§cClose`             |
| BLACK_STAINED_GLASS_PANE | `<space>`             |

### Inventory Title

The plugin uses two following inventory titles. Check if these title conflicts
with other plugins.

1. `§e§lCustom Shops`
2. `§5§lVending Machine`

## Adding Custom Models

You can design your own custom models. To do that, you just have to put the JSON
file into the resource pack and update `paper.json` with additional predicate
mapping to a custom model data (integer). Afterwhich, update the `config.yml`
file to include new model with its corresponding custom model data. More
informative instructions will be added _soon_.

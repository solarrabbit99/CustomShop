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
</tbody></table>

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

## Dependencies

The plugin requires **ProtocolLib**, **Vault** and one of its supporting economy plugins to be
enabled.

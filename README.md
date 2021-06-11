# CustomShop

A Minecraft plugin that implements custom shop designs as a complement to
chest-shops.

## Type of Shops

1. Vending Machines
2. Newt's Briefcase
3. Salesman's Deal (Coming Soon)

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
<a title="Wooden Vending Machine">
<img src="https://i.imgur.com/k9MRtyx.png" decoding="async" width="128"></a>
</td>
<td scope="col" style="width:68px">
<a title="Stone Vending Machine">
<img src="https://i.imgur.com/fvCBhi3.png" decoding="async" width="128"></a>
</td>
<td scope="col" style="width:68px">
<a title="Nether Vending Machine">
<img src="https://i.imgur.com/hVz4AJz.png" decoding="async" width="128"></a>
</td>
<td scope="col" style="width:68px">
<a title="Sand Vending Machine">
<img src="https://i.imgur.com/p3DoI0m.png" decoding="async" width="128"></a>
</td></tr>
<tr>
<td>Wooden Vending Machine</td>
<td>Stone Vending Machine</td>
<td>Nether Vending Machine</td>
<td>Sand Vending Machine</td>
</tr>
<tr>
<td scope="col" style="width: 68px;">
<a title="Prismarine Vending Machine">
<img src="https://i.imgur.com/xL8a4SQ.png" decoding="async" width="128"></a>
</td>
<td scope="col" style="width: 68px;">
<a title="Ice Vending Machine">
<img src="https://i.imgur.com/7aD6xOF.png" decoding="async" width="128"></a>
</td>
<td scope="col" style="width: 68px;">
<a title="Blackstone Vending Machine">
<img src="https://i.imgur.com/y9jYH6i.png" decoding="async" width="128"></a>
</td>
<td scope="col" style="width: 68px;">
<a title="Copper Vending Machine">
<img src="https://i.imgur.com/vmHiPA1.png" decoding="async" width="128"></a>
</td></tr>
<tr>
<td>Prismarine Vending Machine</td>
<td>Ice Vending Machine</td>
<td>Blackstone Vending Machine</td>
<td>Copper Vending Machine</td>
</tr>
<tr>
<td scope="col" style="width: 68px;">
<a title="Amethyst Vending Machine">
<img src="https://i.imgur.com/dvFdAu2.png" decoding="async" width="128"></a>
</td></tr>
<tr>
<td>Amethyst Vending Machine</td>
</tr>
</tbody></table>

### Newt's Briefcase

<table style="margin: auto; text-align: center; max-width: 100%;">
<tbody><tr>
<td scope="col" style="width: 68px;">
<a href="https://imgur.com/soK7Y7L" title="Wooden Newt's Briefcase">
<img src="https://i.imgur.com/soK7Y7L.png" decoding="async" width="128"></a>
</td>
<td scope="col" style="width:68px">
<a href="https://imgur.com/bFFDlsm" title="Stone Newt's Briefcase">
<img src="https://i.imgur.com/bFFDlsm.png" decoding="async" width="128"></a>
</td>
<td scope="col" style="width:68px">
<a href="https://imgur.com/mUPWOaR" title="Nether Newt's Briefcase">
<img src="https://i.imgur.com/mUPWOaR.png" decoding="async" width="128"></a>
</td>
<td scope="col" style="width:68px">
<a href="https://imgur.com/JTlBSW6" title="Sand Newt's Briefcase">
<img src="https://i.imgur.com/JTlBSW6.png" decoding="async" width="128"></a>
</td></tr>
<tr>
<td>Wooden Newt's Briefcase</td>
<td>Stone Newt's Briefcase</td>
<td>Nether Newt's Briefcase</td>
<td>Sand Newt's Briefcase</td>
</tr>
<tr>
<td scope="col" style="width: 68px;">
<a href="https://imgur.com/2PvOCUg" title="Prismarine Newt's Briefcase">
<img src="https://i.imgur.com/2PvOCUg.png" decoding="async" width="128"></a>
</td>
<td scope="col" style="width: 68px;">
<a href="https://imgur.com/zWdzADb" title="Ice Newt's Briefcase">
<img src="https://i.imgur.com/zWdzADb.png" decoding="async" width="128"></a>
</td>
<td scope="col" style="width: 68px;">
<a href="https://imgur.com/7W8Di4O" title="Blackstone Newt's Briefcase">
<img src="https://i.imgur.com/7W8Di4O.png" decoding="async" width="128"></a>
</td>
<td scope="col" style="width: 68px;">
<a href="https://imgur.com/Eni93wR" title="Copper Newt's Briefcase">
<img src="https://i.imgur.com/Eni93wR.png" decoding="async" width="128"></a>
</td></tr>
<tr>
<td>Prismarine Newt's Briefcase</td>
<td>Ice Newt's Briefcase</td>
<td>Blackstone Newt's Briefcase</td>
<td>Copper Newt's Briefcase</td>
</tr>
<tr>
<td scope="col" style="width: 68px;">
<a href="https://imgur.com/VR8mnm3" title="Amethyst Vending Machine">
<img src="https://i.imgur.com/VR8mnm3.png" decoding="async" width="128"></a>
</td></tr>
<tr>
<td>Amethyst Newt's Briefcase</td>
</tr>
</tbody></table>

## Adding Custom Models

You can design your own custom models. To do that, you just have to put the JSON
file into the resource pack and update `paper.json` with additional predicate
mapping to a custom model data (integer). Afterwhich, update the `config.yml`
file to include new model with its corresponding custom model data. More
informative instructions will be added _soon_.

package net.velinquish.compressor.menu;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineacademy.designer.button.Button;
import org.mineacademy.designer.menu.Menu;
import org.mineacademy.designer.menu.impl.MenuStandard;
import org.mineacademy.designer.model.ItemCreator;
import org.mineacademy.designer.model.UIClickLocation;
import org.mineacademy.remain.model.CompDye;

import net.velinquish.compressor.Compressor;
import net.velinquish.utils.Common;
import net.velinquish.utils.ItemBuilder;

public class CompressorMenu extends MenuStandard {
	//TODO ALLOW CLICKING WITHIN THE INVENTORY
	//TODO fix glow
	Compressor plugin = Compressor.getInstance();

	private final Button book;

	private final boolean nextPageButton;

	private Button next = null;

	private final Button coal;
	private final Button iron;
	private final Button gold;
	private final Button diamond;
	private final Button emerald;

	/**
	 *
	 * @param nextPageButton Whether to show the button to go to the next page (another dimension's set of ores)
	 */
	public CompressorMenu(boolean nextPageButton) {
		super(null);

		setSize(1 * 9);
		setTitle("&3Compressor");

		this.nextPageButton = nextPageButton;

		book = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				animateTitle("&3Click the ores");
			}

			@Override
			public ItemStack getItem() {
				return glow(new ItemBuilder(Material.BOOK, Common.colorize("&7(&b&l!&7) &3&lInformation")).lore("&7Compress: &bLeft click", "&7Decompress: &bRight click").build());
			}
		};

		if (nextPageButton)
			next = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				if (player.hasPermission("worlds.access.space"))
					(new SpaceMenu(player.hasPermission("worlds.see.ocean"))).displayTo(player);
				else {
					player.closeInventory();
					plugin.getLangManager().getNode("next-page-no-permission").execute(player);
				}
			}

			@Override
			public ItemStack getItem() {
				return ItemCreator.of(Material.STAINED_GLASS_PANE, "&b&lNext Page", "", "&a&nClick to go forward").color(CompDye.BLACK).build().make();
			}
		};
		else
			next = Button.makeDummy(ItemCreator.of(new ItemStack(Material.AIR)));

		coal = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				transaction(player, click, "coal");
			}

			@Override
			public ItemStack getItem() {
				return glow(new ItemBuilder(Material.COAL_BLOCK, Common.colorize("&8&lCoal")).build());
			}
		};

		iron = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				transaction(player, click, "iron");
			}

			@Override
			public ItemStack getItem() {
				return glow(new ItemBuilder(Material.IRON_BLOCK, Common.colorize("&f&lIron")).build());
			}
		};

		gold = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				transaction(player, click, "gold");
			}

			@Override
			public ItemStack getItem() {
				return glow(new ItemBuilder(Material.GOLD_BLOCK, Common.colorize("&e&lGold")).build());
			}
		};

		diamond = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				transaction(player, click, "diamond");
			}

			@Override
			public ItemStack getItem() {
				return glow(new ItemBuilder(Material.DIAMOND_BLOCK, Common.colorize("&b&lDiamond")).build());
			}
		};

		emerald = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				transaction(player, click, "emerald");
			}

			@Override
			public ItemStack getItem() {
				return glow(new ItemBuilder(Material.EMERALD_BLOCK, Common.colorize("&a&lEmerald")).build());
			}
		};
	}

	@Override
	public ItemStack getItemAt(int slot) {

		if (slot == 0)
			return book.getItem();
		else if (slot == 2)
			return coal.getItem();
		else if (slot == 3)
			return iron.getItem();
		else if (slot == 4)
			return gold.getItem();
		else if (slot == 5)
			return diamond.getItem();
		else if (slot == 6)
			return emerald.getItem();
		else if (slot == 8 && nextPageButton)
			return next.getItem();
		return null;
	}

	private void transaction(Player player, ClickType click, String ore) {
		ItemStack resource = plugin.getItem(ore);
		ItemStack comp = plugin.getItem("comp" + ore);
		if (click.isLeftClick()) {
			if (player.getInventory().containsAtLeast(resource, 64)) {
				player.getInventory().removeItem(new ItemBuilder(resource).amount(64).build());
				player.getInventory().addItem(comp);
			}
		} else if (player.getInventory().containsAtLeast(comp, 1)) {
			player.getInventory().removeItem(comp);
			player.getInventory().addItem(new ItemBuilder(resource).amount(64).build());
		}
	}

	private ItemStack glow(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.addEnchant(Enchantment.LURE, 0, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		return item;
	}

	@Override
	protected String[] getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isActionAllowed(UIClickLocation location, int slot, ItemStack clicked, ItemStack cursor) {
		return (!location.equals(UIClickLocation.MENU));
	}

	class SpaceMenu extends MenuStandard {

		private final Button back;

		private final boolean nextPageButton;
		private Button next = null;

		private final Button redstone;
		private final Button brick;
		private final Button darkbrick;
		private final Button lapis;
		private final Button quartz;

		protected SpaceMenu(boolean showOcean) {
			super(CompressorMenu.this);
			setSize(1 * 9);
			setTitle("&3Space Compressor");

			nextPageButton = showOcean;

			back = new Button() {
				@Override
				public void onClickedInMenu(Player player, Menu menu, ClickType click) {
					(CompressorMenu.this).displayTo(player);
				}

				@Override
				public ItemStack getItem() {
					return ItemCreator.of(Material.STAINED_GLASS_PANE, "&c&lReturn", "", "&a&nClick to go back").color(CompDye.BLACK).build().make();
				}
			};

			if (nextPageButton)
				next = new Button() {
				@Override
				public void onClickedInMenu(Player player, Menu menu, ClickType click) {
					if (player.hasPermission("worlds.access.ocean"))
						(new OceanMenu()).displayTo(player);
					else {
						player.closeInventory();
						plugin.getLangManager().getNode("next-page-no-permission").execute(player);
					}
				}

				@Override
				public ItemStack getItem() {
					return ItemCreator.of(Material.STAINED_GLASS_PANE, "&b&lNext Page", "", "&a&nClick to go forward").color(CompDye.BLACK).build().make();
				}
			};
			else
				next = Button.makeDummy(ItemCreator.of(new ItemStack(Material.AIR)));

			redstone = new Button() {
				@Override
				public void onClickedInMenu(Player player, Menu menu, ClickType click) {
					transaction(player, click, "redstone");
				}

				@Override
				public ItemStack getItem() {
					return glow(new ItemBuilder(Material.REDSTONE_BLOCK, Common.colorize("&4&lRedstone")).build());
				}
			};

			brick = new Button() {
				@Override
				public void onClickedInMenu(Player player, Menu menu, ClickType click) {
					transaction(player, click, "brick");
				}

				@Override
				public ItemStack getItem() {
					return glow(new ItemBuilder(Material.HARD_CLAY, Common.colorize("&6&lBrick")).build());
				}
			};

			darkbrick = new Button() {
				@Override
				public void onClickedInMenu(Player player, Menu menu, ClickType click) {
					transaction(player, click, "darkbrick");
				}

				@Override
				public ItemStack getItem() {
					return glow(new ItemBuilder(Material.NETHER_BRICK, Common.colorize("&8&lDark Brick")).build());
				}
			};

			lapis = new Button() {
				@Override
				public void onClickedInMenu(Player player, Menu menu, ClickType click) {
					transaction(player, click, "lapis");
				}

				@Override
				public ItemStack getItem() {
					return glow(new ItemBuilder(Material.LAPIS_BLOCK, Common.colorize("&9&lLapis")).build());
				}
			};

			quartz = new Button() {
				@Override
				public void onClickedInMenu(Player player, Menu menu, ClickType click) {
					transaction(player, click, "quartz");
				}

				@Override
				public ItemStack getItem() {
					return glow(new ItemBuilder(Material.QUARTZ_BLOCK, Common.colorize("&f&lQuartz")).build());
				}
			};
		}

		@Override
		public ItemStack getItemAt(int slot) {

			if (slot == 0)
				return back.getItem();
			else if (slot == 2)
				return redstone.getItem();
			else if (slot == 3)
				return brick.getItem();
			else if (slot == 4)
				return darkbrick.getItem();
			else if (slot == 5)
				return lapis.getItem();
			else if (slot == 6)
				return quartz.getItem();
			else if (slot == 8 && nextPageButton)
				return next.getItem();
			return null;
		}

		@Override
		protected boolean addReturnButton() {
			return false;
		}

		@Override
		protected String[] getInfo() {
			return null;
		}

		@Override
		protected boolean isActionAllowed(UIClickLocation location, int slot, ItemStack clicked, ItemStack cursor) {
			return (!location.equals(UIClickLocation.MENU));
		}

		class OceanMenu extends MenuStandard {

			private final Button back;

			private final Button nugget;
			private final Button wood;
			private final Button stone;
			private final Button crystal;
			private final Button shard;

			protected OceanMenu() {
				super(CompressorMenu.this);
				setSize(1 * 9);
				// The color will always be the same for the title GUI text
				setTitle("&3Ocean Compressor");

				back = new Button() {
					@Override
					public void onClickedInMenu(Player player, Menu menu, ClickType click) {
						(SpaceMenu.this).displayTo(player);
					}

					@Override
					public ItemStack getItem() {
						return ItemCreator.of(Material.STAINED_GLASS_PANE, "&c&lReturn", "", "&a&nClick to go back").color(CompDye.BLACK).build().make();
					}
				};

				nugget = new Button() {
					@Override
					public void onClickedInMenu(Player player, Menu menu, ClickType click) {
						transaction(player, click, "nugget");
					}

					@Override
					public ItemStack getItem() {
						return glow(new ItemBuilder(Material.GOLD_ORE, Common.colorize("&e&lNugget")).build());
					}
				};

				wood = new Button() {
					@Override
					public void onClickedInMenu(Player player, Menu menu, ClickType click) {
						transaction(player, click, "wood");
					}

					@Override
					public ItemStack getItem() {
						return glow(new ItemBuilder(Material.WOOD, Common.colorize("&6&lWood")).build());
					}
				};

				stone = new Button() {
					@Override
					public void onClickedInMenu(Player player, Menu menu, ClickType click) {
						transaction(player, click, "stone");
					}

					@Override
					public ItemStack getItem() {
						return glow(new ItemBuilder(Material.STONE, Common.colorize("&7&lStone")).build());
					}
				};

				crystal = new Button() {
					@Override
					public void onClickedInMenu(Player player, Menu menu, ClickType click) {
						transaction(player, click, "crystal");
					}

					@Override
					public ItemStack getItem() {
						return glow(new ItemBuilder(Material.SEA_LANTERN, Common.colorize("&b&lCrystal")).build());
					}
				};

				shard = new Button() {
					@Override
					public void onClickedInMenu(Player player, Menu menu, ClickType click) {
						transaction(player, click, "shard");
					}

					@Override
					public ItemStack getItem() {
						return glow(new ItemBuilder(Material.PRISMARINE, Common.colorize("&3&lShard")).build());
					}
				};
			}

			@Override
			public ItemStack getItemAt(int slot) {

				if (slot == 0)
					return back.getItem();
				else if (slot == 2)
					return nugget.getItem();
				else if (slot == 3)
					return wood.getItem();
				else if (slot == 4)
					return stone.getItem();
				else if (slot == 5)
					return crystal.getItem();
				else if (slot == 6)
					return shard.getItem();
				return null;
			}

			@Override
			protected boolean addReturnButton() {
				return false;
			}

			@Override
			protected String[] getInfo() {
				return null;
			}

			@Override
			protected boolean isActionAllowed(UIClickLocation location, int slot, ItemStack clicked, ItemStack cursor) {
				return (!location.equals(UIClickLocation.MENU));
			}

		}
	}
}

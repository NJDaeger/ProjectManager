package com.njdaeger.projectmanager.plugin;

import com.njdaeger.pdk.command.CommandBuilder;
import com.njdaeger.pdk.command.CommandContext;
import com.njdaeger.pdk.command.TabContext;
import com.njdaeger.pdk.command.exception.PDKCommandException;
import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.models.Tag;
import com.njdaeger.projectmanager.plugin.types.ParsedTag;
import com.njdaeger.projectmanager.services.IConfigService;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;

import static org.bukkit.ChatColor.RED;

public class PlotCommands {

    private final ProjectManager plugin;
    private final IConfigService config;

    public PlotCommands(ProjectManager plugin) {
        this.plugin = plugin;
        this.config = plugin.getDataAccess().getConfigService();
        CommandBuilder.of("createplot", "newplot", "cplot")
                .executor(this::createPlot)
                .permissions("plotman.commands.createplot")
                .description("Create a new plot")
                .usage("/createplot <Name of the plot>")
                .build().register(plugin);
        CommandBuilder.of("pmconfig", "plotconfig")
                .executor(this::pmConfig)
                .completer(this::pmConfigTab)
                .permissions("plotman.commands.configure")
                .description("Configure the plot manager")
                .usage("/pmconfig <Name of the plot>")
                .build().register(plugin);

    }


    /*

    Commands:
    /listplots [-sort <closest|furthest|name|id|newest|oldest>]
    |[T][I] - Name

    claims the nearest plot (within 5 blocks)
    /claim [id]

    Current plot actions & info
    /plot [id]
    === Plot Info === [View on Web]
    Name: <name>
        -> inserts name edit command into the chatbar when clicked
        - hover show full name if it cant fit on one line
    Description: <description>
        -> inserts description edit command into chatbar when clicked
        - hover to show full description if it cant fit on one line
    Tags: <tags...>
        -> goes to tag change page when clicked
        - hover to show all tags if they cant fit on one line
    PlotType: <plotType>
        -> goes to plot type change page when clicked
        - Hover will show the rest of the plot type if it cant fit
    Status: <status>
        -> goes to change status page when clicked
        - Hover will show the rest of the status if it cant fit
    Users: <userHistory>
        -> goes to user history change page when clicked
        - Hover will show the user history in a list format showing the most recent people on the plot and their start/end date
        - Regular will just show the list of names or do ellipsis at end if it cant fit whole list
    ViewPermissions: <viewperms>
        -> goes to viewpermission change page when clicked
        - Hover will list all permissions that could be needed to view this plot
        - Regular will just show the one permission if it fits
    Refs: <refList>
    Parent: //maybe link parent in header
    PlotGroup: //maybe link group in header

    /createplottag
    /deleteplottag
    /editplottag
    /listplottags

    /createplottype
    /deleteplottype
    /editplottype
    /listplottypes

    /createplotstatus
    /deleteplotstatus
    /editplotstatus
    /listplotstatus

    /createviewpermission
    /deleteviewpermission
    /editviewpermission
    /listviewpermissions

    /createrecommendpermission
    /deleterecommendpermission
    /editrecommendpermission
    /listrecommendpermissions

    /pmconfig create|edit|list|delete tag|type|status|viewperm|recperm



    /deleteplot [id]


    /createplot <name> -type <type> -levelToView <viewLevel> -minLevel <minLevel> -description <"description"> -tags <tags...> -status <status>




    /


     */

    /*

    flow:
    /createplot 1960s bungalow -type <type> -levelToView <viewLevel> -recommendedLevel <recLevel>



    in chat:
    === Create Plot ===
    Name*: <name>
    Description: [none]
    Tags: [none]
    PlotType: [none]
    Status*: []


    Flags:
    -parent <parentPlotName|parentPlotId>
    -description <"desc">
    -tags <tag1,tag2,..>
    -type <plotType>
    -group <groupName|groupId>
    -status <status>
    -users <user1,user2,..>
    -viewPerm <permission,permission2,..>
    -recPerm <permission,permission2,..>

     */
    private void createPlot(CommandContext context) throws PDKCommandException {
        // /plot 60s bungalow -type
    }

    // /pmconfig create|edit|list|delete tag|type|status|viewperm|recperm
    /*
    /pmconfig edit tag <tagName> <color|name> <newColor|newName>
    /pmconfig edit type <typeName> <name> <newName>
     */
    private void pmConfig(CommandContext context) throws PDKCommandException {

        if (    !context.subCommandAt(0, "edit", true, this::editCommand) &&
                !context.subCommandAt(0, "delete", true, this::deleteCommand) &&
                !context.subCommandAt(0, "create", true, this::createCommand) &&
                !context.subCommandAt(0, "list", true, this::listCommand) &&
                !context.subCommandAt(0, "help", true, this::pmConfigHelp)) {
            context.error("Unknown configuration subcommand. Allowed commands are edit, delete, create, or list. For help, run /pmconfig help");
        }
    }

    private void pmConfigTab(TabContext context) throws PDKCommandException {
        context.completionAt(0, "create", "edit", "list", "delete", "help");
        if (context.first().equalsIgnoreCase("create") || context.first().equalsIgnoreCase("edit") || context.first().equalsIgnoreCase("delete")) {
            context.completionAt(1, "tag", "type", "status", "viewperm", "recperm");

            if (context.first().equalsIgnoreCase("edit") && context.hasArgAt(1)) {
                context.completionIf(ctx -> ctx.argAt(1).equalsIgnoreCase("tag") && context.isLength(3), config.getTags().stream().map(Tag::tag).toArray(String[]::new));
                context.completionIf(ctx -> ctx.argAt(1).equalsIgnoreCase("tag") && context.isLength(4), "color", "name");
            }
            return;
        } else if (context.first().equalsIgnoreCase("list")) {
            context.completionAt(1, "tags", "types", "statuses", "viewperms", "recperms");
            return;
        }

    }

    private void pmConfigHelp(CommandContext context) throws PDKCommandException {

    }

    private void editCommand(CommandContext context) throws PDKCommandException {
        if (
                !context.subCommandAt(1, "tag", true, this::editTag))
            context.error(RED + "Unknown configuration type. Allowed types are tag, type, status, viewperm, or recperm. For help, run /pmconfig help");
    }

    private void deleteCommand(CommandContext context) throws PDKCommandException {

    }

    private void listCommand(CommandContext context) throws PDKCommandException {
        if (
                !context.subCommandAt(1, "tags", true, this::listTags))
            context.error(RED + "Unknown configuration type. Allowed types are tags, types, status, viewperms, or recperms. For help, run /pmconfig help");
    }

    private void createCommand(CommandContext context) throws PDKCommandException {
        if (
                !context.subCommandAt(1, "tag", true, this::createTag))
            context.error(RED + "Unknown configuration type. Allowed types are tag, type, status, viewperm, or recperm. For help, run /pmconfig help");
    }

    // /pmconfig edit tag <tagName> <color|name> <newColor|newName>
    private void editTag(CommandContext context) throws PDKCommandException {
        var tag = context.argAt(2, ParsedTag.class);
        if (tag == null) context.error(RED + "Unknown tag '" + context.argAt(2) + "', please use tab completions to find your desired tag.");

        var attribute = context.argAt(3);
        if (attribute.equalsIgnoreCase("color")) {

            var update = config.updateTagColor(tag, context.argAt(4));
            if (!update.wasSuccess()) context.error(RED + "Tag update unsuccessful. " + update.getMessage());
            else context.pluginMessage("Tag '{0}' color attribute updated to '{1}'", tag.tag(), context.argAt(4) == null ? "default" : context.argAt(4));

        } else if (attribute.equalsIgnoreCase("name")) {

            if (!context.hasArgAt(4)) context.error(RED + "You cannot clear or set a name for a tag to null.");
            var update = config.updateTagName(tag, context.argAt(4));

            if (!update.wasSuccess()) context.error(RED + "Tag update unsuccessful. " + update.getMessage());
            else context.pluginMessage("Tag '{0}' name attribute updated to '{1}'", tag.tag(), context.argAt(4));

        } else context.error(RED + "Unknown attribute '" + attribute + "', valid attributes are 'name' and 'color'.");
    }

    // /pmconfig create tag <name> [color]
    private void createTag(CommandContext context) throws PDKCommandException {
        if (context.isLess(3)) context.notEnoughArgs(RED + "Not enough arguments supplied. Usage: /pmconfig create tag <name> [color]");
        if (context.isGreater(4)) context.tooManyArgs(RED + "Too many arguments supplied. Tag names can't contain spaces.");
        var result = config.createTag(context.argAt(2), context.argAt(3));
        if (!result.wasSuccess()) context.error(RED + "Failed to create tag. " + result.getMessage());
        else context.pluginMessage("Tag '{0}' successfully created.", result.getResult().tag());
    }

    // /pmconfig list tags
    // format:
    /*

    == ddddd Matches ======= Tag List ======= Filter ==
    ? [E][Color] Tagname
    |<-- <- ==== [dddd/dddd] ==== -> -->|

    hover over ? for ID

     */
    private void listTags(CommandContext context) throws PDKCommandException {
        var tags = config.getTags();


    }

    private void deleteTag(CommandContext context) throws PDKCommandException {

    }

}

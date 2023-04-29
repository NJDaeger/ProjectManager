package com.njdaeger.projectmanager.plugin.flags;

import com.njdaeger.pdk.command.CommandContext;
import com.njdaeger.pdk.command.TabContext;
import com.njdaeger.pdk.command.exception.PDKCommandException;
import com.njdaeger.pdk.command.flag.Flag;
import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.models.Tag;
import com.njdaeger.projectmanager.services.IConfigService;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class PlotTags extends Flag<Tag[]> {

    private final IConfigService config;

    public PlotTags() {
        super(Tag[].class, "Set tags on this plot", "-tags tag1,tag2", "tags");
        this.config = ProjectManager.getPlugin(ProjectManager.class).getDataAccess().getConfigService();
    }

    @Override
    public Tag[] parse(CommandContext context, String argument) throws PDKCommandException {
        if (argument == null || argument.isBlank()) return new Tag[0];
        var tags = argument.split(",");
        return Stream.of(tags).map(config::getTagByName).filter(Objects::nonNull).toArray(Tag[]::new);
    }

    @Override
    public void complete(TabContext context) throws PDKCommandException {
        var current = parse(context, context.getCurrent());
        context.completion(config.getTags().stream().filter(tag -> Arrays.stream(current).noneMatch(cur -> cur.id() == tag.id())).map(Tag::tag).toArray(String[]::new));
    }
}

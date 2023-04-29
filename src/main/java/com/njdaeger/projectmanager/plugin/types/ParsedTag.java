package com.njdaeger.projectmanager.plugin.types;

import com.njdaeger.pdk.command.exception.PDKCommandException;
import com.njdaeger.pdk.types.ParsedType;
import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.models.Tag;
import com.njdaeger.projectmanager.services.IConfigService;

public class ParsedTag extends ParsedType<Tag> {

    private final IConfigService config;

    public ParsedTag() {
        this.config = ProjectManager.getPlugin(ProjectManager.class).getDataAccess().getConfigService();
    }

    @Override
    public Tag parse(String input) throws PDKCommandException {
        return config.getTagByName(input);
    }

    @Override
    public Class<Tag> getType() {
        return Tag.class;
    }
}

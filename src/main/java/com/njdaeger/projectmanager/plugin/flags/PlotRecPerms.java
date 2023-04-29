package com.njdaeger.projectmanager.plugin.flags;

import com.njdaeger.pdk.command.CommandContext;
import com.njdaeger.pdk.command.TabContext;
import com.njdaeger.pdk.command.exception.PDKCommandException;
import com.njdaeger.pdk.command.flag.Flag;
import com.njdaeger.projectmanager.models.RecommendedPermission;

public class PlotRecPerms extends Flag<RecommendedPermission[]> {

    public PlotRecPerms() {
        super(RecommendedPermission[].class, "Set the recommended permission required to claim the plot without approval", "-recPerm this_permission,this_other_permission", "recPerm");
    }

    @Override
    public RecommendedPermission[] parse(CommandContext context, String argument) throws PDKCommandException {

        return null;
    }

    @Override
    public void complete(TabContext context) throws PDKCommandException {

    }
}

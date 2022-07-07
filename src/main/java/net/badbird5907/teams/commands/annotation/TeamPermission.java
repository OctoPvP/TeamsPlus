package net.badbird5907.teams.commands.annotation;

import net.badbird5907.teams.object.TeamRank;
import net.octopvp.commander.annotation.DistributeOnMethods;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@DistributeOnMethods
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TeamPermission {
    TeamRank value();
}

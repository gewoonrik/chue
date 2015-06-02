package ch.wisv.chue.events;

import ch.wisv.chue.HueCommand;

/**
 * @author Sander Ploegsma
 */
public abstract class HueEvent extends HueCommand {

    public HueEvent() {

    }

    public abstract void execute();
}

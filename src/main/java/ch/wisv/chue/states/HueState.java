package ch.wisv.chue.states;

import ch.wisv.chue.HueCommand;

/**
 * @author Sander Ploegsma
 */
public abstract class HueState extends HueCommand {

    public HueState() {

    }

    public abstract void execute();
}

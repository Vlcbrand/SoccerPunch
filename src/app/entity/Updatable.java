package app.entity;

import app.SoccerPanel;

/**
 * Update met een {@link SoccerPanel} als parent.
 */
public interface Updatable
{
    void update(final SoccerPanel parent);
}

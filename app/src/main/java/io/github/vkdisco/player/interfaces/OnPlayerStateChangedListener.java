package io.github.vkdisco.player.interfaces;

import io.github.vkdisco.player.PlayerState;

/**
 * OnPlayerStateChangedListener
 * Handles player state changing (started playing, stopped, paused, ...)
 */

public interface OnPlayerStateChangedListener {
    void onPlayerStateChanged(PlayerState state);
}

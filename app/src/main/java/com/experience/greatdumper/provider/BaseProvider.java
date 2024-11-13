package com.experience.greatdumper.provider;

import com.google.firebase.crashlytics.buildtools.reloc.javax.annotation.Nonnull;

public abstract class BaseProvider {

    private ProviderStatus status = ProviderStatus.NOT_LOADED;

    public synchronized ProviderStatus getStatus() {
        return status;
    }

    protected void setStatus(@Nonnull ProviderStatus status) {
        this.status = status;
    }

    public enum ProviderStatus {
        NOT_LOADED,
        LOADED
    }
}
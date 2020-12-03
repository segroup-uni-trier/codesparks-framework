package de.unitrier.st.codesparks.core.data;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class AThreadArtifact extends AArtifact
{
    private String callSite;
    private boolean filtered;

    public AThreadArtifact(String identifier)
    {
        super(identifier, identifier);
        filtered = false;
    }

    public String getCallSite()
    {
        if (callSite == null)
        {
            int i = identifier.indexOf(':');
            if (i < 0)
            {
                return identifier;
            }
            return identifier.substring(0, i);
        }
        return callSite;
    }

    public void setCallSite(String callSite)
    {
        this.callSite = callSite;
    }

    public boolean isFiltered()
    {
        return filtered;
    }

    public void setFiltered(boolean filtered)
    {
        this.filtered = filtered;
    }
}

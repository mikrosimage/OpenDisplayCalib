package fr.hd3d.colortribe.gui.steps;

interface IStep
{
    public void init();
    public boolean canUnLockDependantStep();
    public void unLockDependantStep();
    public void lockDependantStep(String reason);
    public void unLock();
    public void lock(String reasons);
    public void valid();
}

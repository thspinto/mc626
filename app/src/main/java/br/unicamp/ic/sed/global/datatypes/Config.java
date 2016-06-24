package br.unicamp.ic.sed.global.datatypes;

/**
 * RateThisApp configuration.
 */
public class Config{

	private int mCriteriaInstallDays;
    private int mCriteriaLaunchTimes;
    private int mTitleId = 0;
    private int mMessageId = 0;

    /**
     * Constructor with default criteria.
     */
    public Config() {
        this(7, 10);
    }

    /**
     * Constructor.
     * @param criteriaInstallDays
     * @param criteriaLaunchTimes
     */
    public Config(int criteriaInstallDays, int criteriaLaunchTimes) {
        this.mCriteriaInstallDays = criteriaInstallDays;
        this.mCriteriaLaunchTimes = criteriaLaunchTimes;
    }

    public int getmCriteriaInstallDays() {
		return mCriteriaInstallDays;
	}

	public void setmCriteriaInstallDays(int mCriteriaInstallDays) {
		this.mCriteriaInstallDays = mCriteriaInstallDays;
	}

	public int getmCriteriaLaunchTimes() {
		return mCriteriaLaunchTimes;
	}

	public void setmCriteriaLaunchTimes(int mCriteriaLaunchTimes) {
		this.mCriteriaLaunchTimes = mCriteriaLaunchTimes;
	}

	public int getmTitleId() {
		return mTitleId;
	}

	public void setmTitleId(int mTitleId) {
		this.mTitleId = mTitleId;
	}

	public int getmMessageId() {
		return mMessageId;
	}

	public void setmMessageId(int mMessageId) {
		this.mMessageId = mMessageId;
	}
}

package com.enjoyxstudy.selenium.autoexec;

/**
 * @author onozaty
 */
public class Config {

    /** browsers */
    private String[] browsers;

    /** startURL */
    private String startURL;

    /** suiteDir */
    private String suiteDir;

    /** suiteRepo */
    private String suiteRepo;

    /** suiteRepoUsername */
    private String suiteRepoUsername;

    /** suiteRepoPassword */
    private String suiteRepoPassword;

    /** generateSuite */
    private boolean generateSuite;

    /** resultDir */
    private String resultDir;

    /** timeoutInSeconds */
    private int timeoutInSeconds = 60 * 60;

    /**
     * @return browsers
     */
    public String[] getBrowsers() {
        return browsers;
    }

    /**
     * @param browsers browsers
     */
    public void setBrowsers(String[] browsers) {
        this.browsers = browsers;
    }

    /**
     * @return generateSuite
     */
    public boolean isGenerateSuite() {
        return generateSuite;
    }

    /**
     * @param generateSuite generateSuite
     */
    public void setGenerateSuite(boolean generateSuite) {
        this.generateSuite = generateSuite;
    }

    /**
     * @return resultDir
     */
    public String getResultDir() {
        return resultDir;
    }

    /**
     * @param resultDir resultDir
     */
    public void setResultDir(String resultDir) {
        this.resultDir = resultDir;
    }

    /**
     * @return startURL
     */
    public String getStartURL() {
        return startURL;
    }

    /**
     * @param startURL startURL
     */
    public void setStartURL(String startURL) {
        this.startURL = startURL;
    }

    /**
     * @return suiteDir
     */
    public String getSuiteDir() {
        return suiteDir;
    }

    /**
     * @param suiteDir suiteDir
     */
    public void setSuiteDir(String suiteDir) {
        this.suiteDir = suiteDir;
    }

    /**
     * @return suiteRepo
     */
    public String getSuiteRepo() {
        return suiteRepo;
    }

    /**
     * @param suiteRepo suiteRepo
     */
    public void setSuiteRepo(String suiteRepo) {
        this.suiteRepo = suiteRepo;
    }

    /**
     * @return suiteRepoPassword
     */
    public String getSuiteRepoPassword() {
        return suiteRepoPassword;
    }

    /**
     * @param suiteRepoPassword suiteRepoPassword
     */
    public void setSuiteRepoPassword(String suiteRepoPassword) {
        this.suiteRepoPassword = suiteRepoPassword;
    }

    /**
     * @return suiteRepoUsername
     */
    public String getSuiteRepoUsername() {
        return suiteRepoUsername;
    }

    /**
     * @param suiteRepoUsername suiteRepoUsername
     */
    public void setSuiteRepoUsername(String suiteRepoUsername) {
        this.suiteRepoUsername = suiteRepoUsername;
    }

    /**
     * @return timeoutInSeconds
     */
    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    /**
     * @param timeoutInSeconds timeoutInSeconds
     */
    public void setTimeoutInSeconds(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }

}

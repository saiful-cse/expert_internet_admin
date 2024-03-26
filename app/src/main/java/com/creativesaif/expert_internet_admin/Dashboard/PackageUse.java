package com.creativesaif.expert_internet_admin.DeviceUrl;

public class PackageUse {
    private String pkgNmae, pkgQtn;

    public PackageUse(String pkgNmae, String pkgQtn) {
        this.pkgNmae = pkgNmae;
        this.pkgQtn = pkgQtn;
    }

    public PackageUse() {

    }

    public String getPkgNmae() {
        return pkgNmae;
    }

    public void setPkgNmae(String pkgNmae) {
        this.pkgNmae = pkgNmae;
    }

    public String getPkgQtn() {
        return pkgQtn;
    }

    public void setPkgQtn(String pkgQtn) {
        this.pkgQtn = pkgQtn;
    }
}

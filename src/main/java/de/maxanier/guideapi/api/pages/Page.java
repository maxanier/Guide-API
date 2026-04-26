package de.maxanier.guideapi.api.pages;

public class Page implements IPage {


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        return getClass() == o.getClass();
    }

}

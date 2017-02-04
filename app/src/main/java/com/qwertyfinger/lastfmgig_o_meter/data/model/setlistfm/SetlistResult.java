/*
 * Copyright (c) 2017 Andriy Chubko
 */

package com.qwertyfinger.lastfmgig_o_meter.data.model.setlistfm;

import org.threeten.bp.LocalDate;

import java.util.List;

public class SetlistResult {

  private int page;
  private int total;
  private List<Setlist> setlists;

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public List<Setlist> getSetlists() {
    return setlists;
  }

  public void setSetlists(List<Setlist> setlists) {
    this.setlists = setlists;
  }

  public boolean isLastDateMoreRecent(int months) {
    return setlists.get(setlists.size() - 1).getDate().plusMonths(months).isAfter(LocalDate.now());
  }
}

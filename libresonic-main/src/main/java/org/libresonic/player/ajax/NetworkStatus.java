/*
 This file is part of Libresonic.

 Libresonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Libresonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Libresonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2016 (C) Libresonic Authors
 Based upon Subsonic, Copyright 2009 (C) Sindre Mehus
 */
package org.libresonic.player.ajax;

import java.util.Date;

/**
 * @author Sindre Mehus
 */
public class NetworkStatus {
    private final String portForwardingStatusText;
    private final Date portForwardingStatusDate;
    private final String urlRedirectionStatusText;
    private final Date urlRedirectionStatusDate;

    public NetworkStatus(String portForwardingStatusText, Date portForwardingStatusDate,
                         String urlRedirectionStatusText, Date urlRedirectionStatusDate) {
        this.portForwardingStatusText = portForwardingStatusText;
        this.portForwardingStatusDate = portForwardingStatusDate;
        this.urlRedirectionStatusText = urlRedirectionStatusText;
        this.urlRedirectionStatusDate = urlRedirectionStatusDate;
    }

    public String getPortForwardingStatusText() {
        return portForwardingStatusText;
    }

    public Date getPortForwardingStatusDate() {
        return portForwardingStatusDate;
    }

    public String getUrlRedirectionStatusText() {
        return urlRedirectionStatusText;
    }

    public Date getUrlRedirectionStatusDate() {
        return urlRedirectionStatusDate;
    }
}

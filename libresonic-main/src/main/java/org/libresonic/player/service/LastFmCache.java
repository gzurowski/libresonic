/*
 * This file is part of Libresonic.
 *
 *  Libresonic is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Libresonic is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Libresonic.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2014 (C) Sindre Mehus
 */

package org.libresonic.player.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import de.umass.lastfm.cache.Cache;
import de.umass.lastfm.cache.ExpirationPolicy;
import de.umass.lastfm.cache.FileSystemCache;

/**
 * Based on {@link FileSystemCache}, but properly closes files and enforces
 * time-to-live (by ignoring HTTP header directives).
 *
 * @author Sindre Mehus
 * @version $Id$
 */
public class LastFmCache extends Cache {

    private final File cacheDir;
    private final long ttl;

    public LastFmCache(File cacheDir, final long ttl) {
        this.cacheDir = cacheDir;
        this.ttl = ttl;

        setExpirationPolicy(new ExpirationPolicy() {
            @Override
            public long getExpirationTime(String method, Map<String, String> params) {
                return ttl;
            }
        });
    }

    @Override
    public boolean contains(String cacheEntryName) {
        return getXmlFile(cacheEntryName).exists();
    }

    @Override
    public InputStream load(String cacheEntryName) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(getXmlFile(cacheEntryName));
            return new ByteArrayInputStream(IOUtils.toByteArray(in));
        } catch (Exception e) {
            return null;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Override
    public void remove(String cacheEntryName) {
        getXmlFile(cacheEntryName).delete();
        getMetaFile(cacheEntryName).delete();
    }

    @Override
    public void store(String cacheEntryName, InputStream inputStream, long expirationDate) {
        createCache();

        OutputStream xmlOut = null;
        OutputStream metaOut = null;
        try {
            File xmlFile = getXmlFile(cacheEntryName);
            xmlOut = new FileOutputStream(xmlFile);
            IOUtils.copy(inputStream, xmlOut);

            File metaFile = getMetaFile(cacheEntryName);
            Properties properties = new Properties();

            // Note: Ignore the given expirationDate, since Last.fm sets it to just one day ahead.
            properties.setProperty("expiration-date", Long.toString(getExpirationDate()));

            metaOut = new FileOutputStream(metaFile);
            properties.store(metaOut, null);
        } catch (Exception e) {
            // we ignore the exception. if something went wrong we just don't cache it.
        } finally {
            IOUtils.closeQuietly(xmlOut);
            IOUtils.closeQuietly(metaOut);
        }
    }

    private long getExpirationDate() {
        return System.currentTimeMillis() + ttl;
    }

    private void createCache() {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    @Override
    public boolean isExpired(String cacheEntryName) {
        File f = getMetaFile(cacheEntryName);
        if (!f.exists()) {
            return false;
        }
        InputStream in = null;
        try {
            Properties p = new Properties();
            in = new FileInputStream(f);
            p.load(in);
            long expirationDate = Long.valueOf(p.getProperty("expiration-date"));
            return expirationDate < System.currentTimeMillis();
        } catch (Exception e) {
            return false;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Override
    public void clear() {
        for (File file : cacheDir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }

    private File getXmlFile(String cacheEntryName) {
        return new File(cacheDir, cacheEntryName + ".xml");
    }

    private File getMetaFile(String cacheEntryName) {
        return new File(cacheDir, cacheEntryName + ".meta");
    }
}

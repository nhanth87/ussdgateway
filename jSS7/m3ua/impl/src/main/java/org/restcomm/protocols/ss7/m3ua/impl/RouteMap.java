package org.restcomm.protocols.ss7.m3ua.impl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.jctools.maps.NonBlockingHashMap;

/**
 *
 * @author amit bhayani
 *
 * @param <K>
 * @param <V>
 */
@JacksonXmlRootElement(localName = "route")
public class RouteMap<K, V> extends NonBlockingHashMap<K, V> {

}

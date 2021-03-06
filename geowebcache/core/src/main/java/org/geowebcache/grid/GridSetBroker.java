/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Arne Kepp, OpenGeo, Copyright 2009
 */
package org.geowebcache.grid;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GridSetBroker {
    private static Log log = LogFactory.getLog(GridSetBroker.class);
    
    public final GridSet WORLD_EPSG4326;
    
    public final GridSet WORLD_EPSG3857;

    Hashtable<String,GridSet> gridSets = new Hashtable<String,GridSet>();
    
    public GridSetBroker(boolean useEPSG900913, boolean useGWC11xNames) {
        String unprojectedName = "GlobalCRS84Geometric";
        String mercatorName = "GoogleMapsCompatible";
        
        if(useGWC11xNames) {
            unprojectedName = "EPSG:4326";
            if(useEPSG900913) {
                mercatorName = "EPSG:900913";
            } else {
                mercatorName = "EPSG:3857";
            }
            
        }
        
        log.debug("Adding " + unprojectedName);
        WORLD_EPSG4326 = GridSetFactory.createGridSet(
                unprojectedName, 
                SRS.getEPSG4326(), 
                BoundingBox.WORLD4326,
                false,
                GridSetFactory.DEFAULT_LEVELS,
                null,
                0.00028,
                256,
                256,
                true );
        gridSets.put(WORLD_EPSG4326.name, WORLD_EPSG4326);
        
        if(useEPSG900913) {
            log.debug("Adding EPSG:900913 grid set for Spherical Mercator / GoogleMapsCompatible");
            
            WORLD_EPSG3857 = GridSetFactory.createGridSet(
                    mercatorName,
                    SRS.getEPSG900913(),
                    BoundingBox.WORLD3857,
                    false,
                    GridSetFactory.DEFAULT_LEVELS,
                    null,
                    0.00028,
                    256,
                    256,
                    false );
        } else {
            log.debug("Adding EPSG:3857 grid set for Spherical Mercator / GoogleMapsCompatible");
            
            WORLD_EPSG3857 = GridSetFactory.createGridSet(
                    mercatorName, 
                    SRS.getEPSG3857(),
                    BoundingBox.WORLD3857,
                    false,
                    GridSetFactory.DEFAULT_LEVELS,
                    null,
                    0.00028,
                    256,
                    256,
                    false );
        }
        gridSets.put(WORLD_EPSG3857.name, WORLD_EPSG3857);
        
        log.debug("Adding GlobalCRS84Pixel");
        GridSet GlobalCRS84Pixel = GridSetFactory.createGridSet(
                "GlobalCRS84Pixel",
                SRS.getEPSG4326(),
                BoundingBox.WORLD4326,
                true,
                scalesCRS84PixelResolutions(),
                null,
                null,
                0.00028,
                null,
                256,
                256,
                true );
        
        gridSets.put(GlobalCRS84Pixel.name, GlobalCRS84Pixel);
        
        log.debug("Adding GlobalCRS84Scale");
        GridSet GlobalCRS84Scale = GridSetFactory.createGridSet(
                "GlobalCRS84Scale",
                SRS.getEPSG4326(),
                BoundingBox.WORLD4326,
                true,
                scalesCRS84ScaleResolutions(),
                null,
                null,
                0.00028,
                null,
                256,
                256,
                true );
        
        gridSets.put(GlobalCRS84Scale.name, GlobalCRS84Scale);
    }
    
    public GridSet get(String gridSetId) {
        return gridSets.get(gridSetId);
    }
    
    public Hashtable<String,GridSet> getGridSets() {
        return gridSets;
    }
    
    public void put(GridSet gridSet) {
        if(gridSets.contains(gridSet.getName())) {
            log.warn("Duplicate grid set " + gridSet.getName() + ", "
                    + "removing previous instance, but it may still be referenced by layers.");
            
            gridSets.remove(gridSet.getName());
        }
        
        log.debug("Adding " + gridSet.getName());
        gridSets.put(gridSet.getName(), gridSet);
    }
    
    private double[] scalesCRS84PixelResolutions() {
        double[] scalesCRS84Pixel = new double[18];
        scalesCRS84Pixel[0] = 2;
        scalesCRS84Pixel[1] = 1;
        scalesCRS84Pixel[2] = 0.5;                                  // 30
        scalesCRS84Pixel[3] = scalesCRS84Pixel[2] * (2.0 / 3.0);    // 20
        scalesCRS84Pixel[4] = scalesCRS84Pixel[2] / 3.0;            // 10
        scalesCRS84Pixel[5] = scalesCRS84Pixel[4] / 2.0;            // 5
        scalesCRS84Pixel[6] = scalesCRS84Pixel[4] / 5.0;            // 2
        scalesCRS84Pixel[7] = scalesCRS84Pixel[4] / 10.0;           // 1
        scalesCRS84Pixel[8] = (5.0 / 6.0) * 1E-2;                   // 30'' = 8.33E-3
        scalesCRS84Pixel[9] = scalesCRS84Pixel[8] / 2.0;            // 15''
        scalesCRS84Pixel[10] = scalesCRS84Pixel[9] / 3.0;           // 5''
        scalesCRS84Pixel[11] = scalesCRS84Pixel[9] / 5.0;           // 3''
        scalesCRS84Pixel[12] = scalesCRS84Pixel[11] / 3.0;          // 1''
        scalesCRS84Pixel[13] = scalesCRS84Pixel[12] / 2.0;          // 0.5''
        scalesCRS84Pixel[14] = scalesCRS84Pixel[13] * (3.0 / 5.0);  // 0.3''
        scalesCRS84Pixel[15] = scalesCRS84Pixel[14] / 3.0;          // 0.1''
        scalesCRS84Pixel[16] = scalesCRS84Pixel[15] * (3.0 / 10.0); // 0.03''
        scalesCRS84Pixel[17] = scalesCRS84Pixel[16] / 3.0;          // 0.01''
        
        return scalesCRS84Pixel;
    }
    
    private double[] scalesCRS84ScaleResolutions() {
        double[] scalesCRS84Pixel = {
                1.25764139776733,
                0.628820698883665,
                0.251528279553466,
                0.125764139776733,
                6.28820698883665E-2,
                2.51528279553466E-2,
                1.25764139776733E-2,
                6.28820698883665E-3,
                2.51528279553466E-3,
                1.25764139776733E-3,
                6.28820698883665E-4,
                2.51528279553466E-4,
                1.25764139776733E-4,
                6.28820698883665E-5,
                2.51528279553466E-5,
                1.25764139776733E-5,
                6.28820698883665E-6,
                2.51528279553466E-6,
                1.25764139776733E-6,
                6.28820698883665E-7,
                2.51528279553466E-7
        };
        
        return scalesCRS84Pixel;
    }
}
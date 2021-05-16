package com.stellarsoftware.beam;

import java.util.*;         // ArrayList
import javax.swing.*;       // JMenuItem


@SuppressWarnings("serial")

/**
  *  This file has just one class: OEJIF that extends EJIF,
  *  supplying EJIF's abstract method parse(). 
  *  The function of parse() is to set values into DMF.giFlags[] and RT13.surfs[][]. 
  *
  *  It implements Constants via EJIF. 
  *  The ACTUAL PARSING WORK is done by getOptFieldAttrib(String s), bottom of this file.
  *
  *  parse() has no dirty bit worksavers; it always parses. 
  *
  *  @author M.Lampton (c) 2004-2012 STELLAR SOFTWARE all rights reserved.
  */
class OEJIF extends EJIF 
{
    // public static final long serialVersionUID = 42L;

//    public static int oF2I[] = new int[MAXFIELDS];
//    public static int oI2F[] = new int[ONPARMS];
//    public static String oglasses[] = new String[JMAX+1];
    // public, so that DMF can check validity against MEJIF.mglasses[].

    public OEJIF(int iXY, String gfname) 
    {
        super(0, iXY, ".OPT", gfname, MAXSURFS, new OptParser()); // call EJIF
        myFpath = gfname;                        // field of EJIF.
    }

    public OptParser parser() {
        return (OptParser) parser;
    }


//    void parse()
//    // replaces the abstract parse() in EJIF.
//    // This is NOT PRIVATE; DMF:vMasterParse calls it, triggered by blinker, etc.
//    {
//        adjustables = new ArrayList<Adjustment>();
//
//        // First, communicate EJIF results to DMF.giFlags[]
//        // vPreParse() takes care of parsing title line.
//
//        int status[] = new int[NGENERIC];
//        vPreParse(status);
//        DMF.giFlags[OPRESENT] = status[GPRESENT];
//        DMF.giFlags[ONLINES]  = status[GNLINES];
//        DMF.giFlags[ONSURFS]  = nsurfs = status[GNRECORDS];
//        DMF.giFlags[ONFIELDS] = nfields = status[GNFIELDS];
//        if (nsurfs < 1)
//          return;
//
//        //-------------set up default surface data--------------
//
//        DMF.giFlags[OMEDIANEEDED] = FALSE; // ok=notNeeded; TRUE=needed
//
//        for (int j=1; j<=MAXSURFS; j++)
//        {
//            oglasses[j] = "";
//            for (int ia=0; ia<ONPARMS; ia++)
//              RT13.surfs[j][ia] = -0.0; // minus zero means blank entry.
//
//            RT13.surfs[j][OREFRACT] = 1.0;
//        }
//
//        for (int f=0; f<MAXFIELDS; f++)
//          headers[f] = "";
//
//        for (int r=0; r<JMAX; r++)
//        {
//            typetag[r] = ':';
//            for (int f=0; f<MAXFIELDS; f++)
//              cTags[r][f] = ':';
//        }
//
//        //----get headers using getFieldTrim()---------------
//
//        for (int f=0; f<nfields; f++)
//          headers[f] = getFieldTrim(f, 1);
//
//        //--build the two one-way lookup tables for field IDs-------
//
//        for (int i=0; i<ONPARMS; i++)   // ABSENT = -1
//          oI2F[i] = ABSENT;
//
//        for (int f=0; f<MAXFIELDS; f++) // ABSENT = -1
//          oF2I[f] = ABSENT;
//
//        int ntries=0, nunrecognized=0;
//        for (int f=0; f<nfields; f++)
//        {
//            ntries++;
//            int iatt = OptParser.getOptFieldAttrib(headers[f]); // bottom of this file...
//            oF2I[f] = iatt;
//            if ((iatt > ABSENT) && (iatt < ONPARMS))
//              oI2F[iatt] = f;
//            else
//              nunrecognized++;  // unused.
//        }
//
//        //----gather individual surface types: nonnumerical data-------
//        //----a grating is not a type: it's a groovy mirror or lens----
//
//        for (int jsurf=1; jsurf<=nsurfs; jsurf++)
//        {
//            RT13.surfs[jsurf][OTYPE] = OTLENS;  // default
//        }
//
//        //-----first parse the optics type column---------
//
//        int ifield = oI2F[OTYPE];
//        if (ifield > ABSENT)
//          for (int jsurf=1; jsurf<=nsurfs; jsurf++)
//          {
//              String s = getFieldTrim(ifield, 2+jsurf);
//              char c0 = U.getCharAt(s, 0);
//              char c2 = U.getCharAt(s.toUpperCase(), 2);
//              char c4 = U.getCharAt(s.toUpperCase(), 4);
//              switch(c0)
//              {
//                 case 'b': // bimodal lens front="bif" "bir" "bib" "bim"
//                 case 'B': if (c2 == 'F')
//                               RT13.surfs[jsurf][OTYPE]  = OTBLFRONT;
//                            if ((c2 == 'R') || (c2 == 'B'))
//                               RT13.surfs[jsurf][OTYPE] = OTBLBACK;
//                            if (c2 == 'M')
//                               RT13.surfs[jsurf][OTYPE] = OTBMIRROR;
//                            if (c2 == 'T')
//                               RT13.surfs[jsurf][OTYPE] = OTTERMINATE;
//                            break;
//
//                 case 'c':     // coordinate breaks
//                 case 'C': if (c2 == 'I')
//                           {
//                               RT13.surfs[jsurf][OTYPE] = OTCBIN;
//                               break;
//                           }
//                           if (c2 == 'O')
//                           {
//                               RT13.surfs[jsurf][OTYPE] = OTCBOUT;
//                               break;
//                           }
//                           break;
//
//                 case 'i': // iris
//                 case 'I': RT13.surfs[jsurf][OTYPE]
//                             = (c4=='A') ? OTIRISARRAY : OTIRIS; break;
//
//                 case 'G': RT13.surfs[jsurf][OTYPE]
//                             = (c2=='C') ? OTGSCATTER : OTMIRROR; break;  // A195
//
//                 case 'l':
//                 case 'L': RT13.surfs[jsurf][OTYPE]
//                             = (c4=='A') ? OTLENSARRAY : OTLENS; break;
//
//                 case 'm':
//                 case 'M': RT13.surfs[jsurf][OTYPE]
//                             = (c4=='A') ? OTMIRRARRAY : OTMIRROR; break;
//
//                 // phantom is just a refracting surface with equal indices.
//
//                 case 'r':
//                 case 'R': RT13.surfs[jsurf][OTYPE] = OTRETRO; break;
//
//                 // case 's':  // spider: replaced by iris with legs.
//
//                 case 's':
//                 case 'S':  RT13.surfs[jsurf][OTYPE] = OTGSCATTER; break;  // A195 Gaussian scatter
//
//                 case 't':
//                 case 'T':  RT13.surfs[jsurf][OTYPE] = OTTERMINATE; break;  // Term is alternate to BiTerm
//
//                 case 'u':
//                 case 'U':  RT13.surfs[jsurf][OTYPE] = OTUSCATTER; break;  // A195 uniform scatter
//
//                 case 'd':     // optical path distorters
//                 case 'D':
//                 case 'w':
//                 case 'W': RT13.surfs[jsurf][OTYPE] = OTDISTORT; break;
//
//
//                 default: RT13.surfs[jsurf][OTYPE] = OTLENS; break;
//               }
//               typetag[jsurf] = getTag(ifield, 2+jsurf);
//          };
//
//        //--------parse the optics forms column----------
//
//        for (int jsurf=1; jsurf<=nsurfs; jsurf++)
//          RT13.surfs[jsurf][OFORM] = OFELLIP;  // default
//
//        ifield = oI2F[OFORM];
//        if (ifield > ABSENT)
//          for (int jsurf=1; jsurf<=nsurfs; jsurf++)
//          {
//              //---enforce idea of all arrays rectangular-------
//              int i = (int) RT13.surfs[jsurf][OTYPE];
//              boolean bArray = ((i==OTLENSARRAY) || (i==OTMIRRARRAY) || (i==OTIRISARRAY));
//
//              String s = getFieldTrim(ifield, 2+jsurf);
//              char c0 = U.getCharAt(s, 0);
//              char c1 = U.getCharAt(s, 1);
//              RT13.surfs[jsurf][OFORM] = OFELLIP;
//              if ((c0=='s') || (c1=='s'))
//                RT13.surfs[jsurf][OFORM] += OFIRECT;
//              if ((c0=='S') || (c1=='S') || bArray)
//                RT13.surfs[jsurf][OFORM] += OFORECT;
//          }
//
//
//        //----------refraction: sometimes numerical data--------------
//        //---if refraction LUT is needed, OREFRACT will be NaN.----
//
//        for (int jsurf=1; jsurf<=nsurfs; jsurf++)
//          oglasses[jsurf] = new String("");       // default: all numeric
//
//        boolean bAllRefractNumeric = true;       // default: true;
//
//        ifield = oI2F[OREFRACT];
//        if (ifield > ABSENT)
//          for (int jsurf=1; jsurf<=nsurfs; jsurf++)
//          {
//              oglasses[jsurf] = getFieldTrim(ifield, 2+jsurf);
//              RT13.surfs[jsurf][OREFRACT] = U.suckDouble(oglasses[jsurf]);
//              if (Double.isNaN(RT13.surfs[jsurf][OREFRACT]))
//                bAllRefractNumeric = false;
//              if (0.0 == RT13.surfs[jsurf][OREFRACT])
//                RT13.surfs[jsurf][OREFRACT] = 1.0;
//          }
//        DMF.giFlags[OMEDIANEEDED] = bAllRefractNumeric ? FALSE : TRUE;
//
//
//        //-------Now get numerical data records-------------------
//        //---except ABSENT, OFORM, OTYPE, OREFRACT, OGROUP-----
//
//        boolean bAllOtherNumeric = true;
//        int badline=0, badfield=0, osyntaxerr=0;
//        double d;
//        for (int jsurf=1; jsurf<=nsurfs; jsurf++)
//        {
//            for (int f=0; f<nfields; f++)
//            {
//                int ia = oF2I[f];   // attribute of this surface
//
//                if (ia == OTYPE)    // types were analyzed above...
//                  continue;
//                if (ia == OFORM)    // forms were analyzed above...
//                  continue;
//                if (ia == OREFRACT) // refraction analyzed above...
//                  continue;
//                if (ia == OGROUP)   // group analyzed above...
//                  continue;
//                if (ia > ABSENT)    // all numerical fields can overwrite negZero here.
//                {
//                    // first, fill in the datum....
//                    d = RT13.surfs[jsurf][ia] = getFieldDouble(f, 2+jsurf);
//
//                    // then check for trouble and correct it....
//                    if (U.isNegZero(d) && isAdjustable(jsurf, ia))
//                      RT13.surfs[jsurf][ia] = +0.0;         // active
//
//                    // d = value, or NaN, or -0.0=unused, or +0.0=in use.
//                    // now, U.NegZero(d) indicates no use whatsoever.
//
//                    if (Double.isNaN(d))
//                    {
//                        bAllOtherNumeric = false;
//                        badline = jsurf+2;
//                        badfield = f;
//                        osyntaxerr = badfield + 100*badline;
//                        break;
//                    }
//
//                    // allow defined shape to determine asphericity...
//                    if ((ia == OSHAPE) && !U.isNegZero(d))
//                      RT13.surfs[jsurf][OASPHER] = d - 1.0;
//
//                    // allow defined radii of curvature to determine curvature...
//                    if ((ia == ORAD) && (d != 0.0))
//                      RT13.surfs[jsurf][OCURVE] = 1.0/d;
//                    if ((ia == ORADX) && (d != 0.0))
//                      RT13.surfs[jsurf][OCURVX] = 1.0/d;
//                    if ((ia == ORADY) && (d != 0.0))
//                      RT13.surfs[jsurf][OCURVY] = 1.0/d;
//                }
//            }
//            if (osyntaxerr > 0)
//              break;
//        }
//
//        DMF.giFlags[OSYNTAXERR] = osyntaxerr;
//        if (osyntaxerr > 0)
//          return;
//
//        //----data are now cleansed stashed & indexed------------
//        //-------Perform all the post-parse cleanup here------------
//
//        DMF.giFlags[ONADJ] = iParseAdjustables(nsurfs);
//
//        //----force all CoordBreaks to be planar? or not? rev 168----
//        // for (int j=1; j<nsurfs; j++)
//        //   if ((RT13.surfs[j][OTYPE]==OTCBIN) || (RT13.surfs[j][OTYPE]==OTCBOUT))
//        //   {
//        //       RT13.surfs[j][OPROFILE] = OSPLANO;
//        //       for (int iatt=OCURVE; iatt<=OZ35; iatt++)
//        //         RT13.surfs[j][iatt] = 0.0;
//        //   }
//
//
//        //-------evaluate diameters DIAX, DIAY-------------------
//        for (int j=1; j<=nsurfs; j++)
//        {
//            boolean bM = RT13.surfs[j][OIDIAM] > 0.0;
//            boolean bX = RT13.surfs[j][OIDIAX] > 0.0;
//            boolean bY = RT13.surfs[j][OIDIAY] > 0.0;
//            if (!bX)
//            {
//                if (bM)
//                  RT13.surfs[j][OIDIAX] = RT13.surfs[j][OIDIAM];
//                else if (bY)
//                  RT13.surfs[j][OIDIAX] = RT13.surfs[j][OIDIAY];
//            }
//            if (!bY)
//            {
//                if (bM)
//                  RT13.surfs[j][OIDIAY] = RT13.surfs[j][OIDIAM];
//                else if (bX)
//                  RT13.surfs[j][OIDIAY] = RT13.surfs[j][OIDIAX];
//            }
//            bM = RT13.surfs[j][OODIAM] > 0.0;
//            bX = RT13.surfs[j][OODIAX] > 0.0;
//            bY = RT13.surfs[j][OODIAY] > 0.0;
//            if (!bX)
//            {
//                if (bM)
//                  RT13.surfs[j][OODIAX] = RT13.surfs[j][OODIAM];
//                else if (bY)
//                  RT13.surfs[j][OODIAX] = RT13.surfs[j][OODIAY];
//            }
//            if (!bY)
//            {
//                if (bM)
//                  RT13.surfs[j][OODIAY] = RT13.surfs[j][OODIAM];
//                else if (bX)
//                  RT13.surfs[j][OODIAY] = RT13.surfs[j][OODIAX];
//            }
//        }
//        boolean bAllDiamsPresent = true;
//        for (int j=1; j<=nsurfs; j++)
//        {
//            boolean bX = RT13.surfs[j][OODIAX] > 0.0;
//            boolean bY = RT13.surfs[j][OODIAY] > 0.0;
//            if (!bX || !bY)
//              bAllDiamsPresent = false;
//        }
//        DMF.giFlags[OALLDIAMSPRESENT] = bAllDiamsPresent ? TRUE : FALSE; // ints!
//
//        //------------set the Euler angle matrix------------------
//
//        RT13.setEulers();
//
//        //------------Test each surface for groovyness----------------
//        for (int j=1; j<=nsurfs; j++)
//        {
//            boolean bGroovy = false;
//            for (int kg=OORDER; kg<OGROOVY; kg++)
//              if (RT13.surfs[j][kg] != 0.0)
//                bGroovy = true;
//            RT13.surfs[j][OGROOVY] = bGroovy ? 1.0 : 0.0;
//        }
//
//        //---------verify that array diams are within cells------------
//        for (int j=1; j<nsurfs; j++)
//        {
//            int i = (int) RT13.surfs[j][OTYPE];
//            boolean bArray = ((i==OTLENSARRAY) || (i==OTMIRRARRAY) || (i==OTIRISARRAY));
//            if (bArray)
//            {
//                double diax = RT13.surfs[j][OODIAX];
//                if (U.isNegZero(diax))
//                  diax = RT13.surfs[j][OODIAM];
//                int nx = (int) RT13.surfs[j][ONARRAYX];
//                if ((diax<=TOL) || (nx<1))
//                  continue; // continue, not return!
//                double px = diax/nx;
//                if(RT13.surfs[j][OIDIAX] > diax/nx)
//                  RT13.surfs[j][OIDIAX] = diax/nx;
//
//                double diay = RT13.surfs[j][OODIAM];
//                int ny = (int) RT13.surfs[j][ONARRAYY];
//                if ((diay<=TOL) || (ny<1))
//                  continue; // continue, not return!
//                double py = diay/ny;
//                if (RT13.surfs[j][OIDIAM] > diay/ny)
//                  RT13.surfs[j][OIDIAM] = diay/ny;
//
//                if (nx*ny > MAXHOLES)
//                  RT13.surfs[j][ONARRAYY] = (MAXHOLES)/nx;
//            }
//        }
//
//        //---------classify each surface profile for solvers------------
//        //---CX cyl & torics: ternary logic. See MNOTES May 25 2007-----
//        //
//        //              C=blank,   zero,  nonzero
//        //             ---------  ------  -------
//        //   CX=blank:   PLANO     PLANO   CONIC
//        //    CX=zero:   PLANO     PLANO   CYCYL
//        // CX=nonzero:   CXCYL     CXCYL   TORIC
//        //
//        // Adjustability:  see below.
//        // Special case added in A119 Dec 2010:
//        //    CX=nonblank and CY=nonblank: OSBICONIC
//        //
//        //  TERNARY LOGIC: see lines 453-463.
//
//        boolean badZern = false;     // flag for single warning message at end
//
//        for (int j=1; j<=nsurfs; j++)
//        {
//            double  ce = RT13.surfs[j][OCURVE];
//            double  cx = RT13.surfs[j][OCURVX];
//            double  cy = RT13.surfs[j][OCURVY];
//            double  ae = RT13.surfs[j][OASPHER];
//            double  ax = RT13.surfs[j][OASPHX];
//            double  ay = RT13.surfs[j][OASPHY];
//
//            //---TERNARY LOGIC EVALUATOR starts here-----
//            //---three states: empty field, entry=0, entry is nonzero------
//            //---Determined by Curv and Cx; Cy has no influence---------
//
//            boolean bCEactive = (ce!=0.0) || isAdjustable(j, OCURVE);
//            boolean bCXactive = (cx!=0.0) || isAdjustable(j, OCURVX);
//            int tce = bCEactive ? 2 : U.isNegZero(ce) ? 0 : 1;   // 0, 1, or 2.
//            int tcx = bCXactive ? 2 : U.isNegZero(cx) ? 0 : 1;   // 0, 1, or 2.
//            int tg[] = { OSPLANO, OSPLANO, OSCONIC, OSPLANO, OSPLANO, OSYCYL, OSXCYL,  OSXCYL,  OSTORIC};
//            int arg = tce + 3*tcx;
//            int iProfile = tg[arg];
//
//            // String osnames[] = {"OSPLANO", "OSPLANO", "OSCONIC", "OSPLANO", "OSPLANO", "OSYCYL", "OSXCYL", "OSXCYL", "OSTORIC"};
//            // if (j==1)
//            //   System.out.println("OEJIF ternary logic result: iProfile = "+iProfile+"  "+osnames[arg]);
//
//            // Rules, A190:
//            // PolyCyl: requires axis=x, Cx=0 (uncurved in XZ plane), Curve=Nonzero, poly in y: OSYCYL.
//            // CircCyl: can have axis=x, Cx=0 (uncurved in XZ plane), Curve=Nonzero, no poly;  OSYCYL.
//            // CircCyl: or have  axis=y, Cx=nonzero, Curve=blank or zero, no poly terms:  OSXCYL
//            // Toric:   requires Curve=nonzero, Cx=nonzero.
//            //
//            //----ternary logic evaluator ends here-----
//
//            //----test for biconic---------------------
//            //--this can overwrite the ternary logic----
//
//            boolean bBCXactive = !U.isNegZero(cx) || isAdjustable(j,OCURVX);
//            boolean bBCYactive = !U.isNegZero(cy) || isAdjustable(j,OCURVY);
//            boolean bBAXactive = !U.isNegZero(ax) || isAdjustable(j,OASPHX);
//            boolean bBAYactive = !U.isNegZero(ay) || isAdjustable(j,OASPHY);
//            if (bBCXactive && bBCYactive && bBAXactive && bBAYactive)
//              iProfile = OSBICONIC;
//
//            //-------polynomial----------
//
//            boolean bPoly = false;
//            for (int i=OA1; i<=OA14; i++)
//              if ((0 != RT13.surfs[j][i]) || isAdjustable(j,i))
//                bPoly = true;
//
//            //----Zernike flag and diameter test  -----------------
//            //---CoordinateBreaks set zernikes to zero, not -0  -----
//            //---here it is important to accept zeros---------------
//
//            boolean bZern = false;
//            for (int i=OZ00; i<=OZ35; i++)
//              if ((0 != RT13.surfs[j][i]) || isAdjustable(j,i))
//                bZern = true;
//
//            if (bZern && (RT13.surfs[j][OODIAM] == 0.0))
//              badZern = true; // gives warning "Zernikes require Diameters"
//
//            //-------upgrade if poly or zern is present--------
//
//            if (bPoly)
//              switch (iProfile)
//              {
//                 case OSPLANO:  iProfile = OSPOLYREV; break;
//                 case OSCONIC:  iProfile = OSPOLYREV; break;
//                 case OSXCYL:   iProfile = OSTORIC; break;
//                 case OSYCYL:   iProfile = OSTORIC; break;
//                 case OSTORIC:  iProfile = OSTORIC; break;
//              }
//
//            if (bZern)
//              iProfile = (iProfile==OSTORIC) ? OSZERNTOR : OSZERNREV;
//
//            if (RT13.surfs[j][ORGAUSS] > 0.)  // found a Gaussian surface profile
//            {
//                iProfile = OSGAUSS;
//            }
//            //---------apply hints to conic or cyl, not higher----------
//
//            switch (iProfile)
//            {
//               case OSCONIC:
//                  if ('<' == typetag[j])  iProfile = OSCONICLT;
//                  if ('>' == typetag[j])  iProfile = OSCONICGT;
//                  break;
//               case OSXCYL:
//                  if ('<' == typetag[j])  iProfile = OSXCYLLT;
//                  if ('>' == typetag[j])  iProfile = OSXCYLGT;
//                  break;
//               case OSYCYL:
//                  if ('<' == typetag[j])  iProfile = OSYCYLLT;
//                  if ('>' == typetag[j])  iProfile = OSYCYLGT;
//                  break;
//            }
//            // System.out.println("OEJIF parse() surface= "+j+" finds iProfile= "+iProfile+"  "+sProfiles[iProfile]);
//
//            RT13.surfs[j][OPROFILE] = iProfile;
//        }
//
//        //---------feel out dOsize for getDelta()----------
//
//        dOsize = 0.0;
//        for (int j=1; j<=nsurfs; j++)
//        {
//           dOsize = Math.max(dOsize, Math.abs(RT13.surfs[j][OX]));
//           dOsize = Math.max(dOsize, Math.abs(RT13.surfs[j][OY]));
//           dOsize = Math.max(dOsize, Math.abs(RT13.surfs[j][OZ]));
//           dOsize = Math.max(dOsize, Math.abs(RT13.surfs[j][OODIAM]));
//           dOsize = Math.max(dOsize, Math.abs(RT13.surfs[j][OODIAX]));
//        }
//        if (dOsize < TOL)
//          dOsize = 1.0;
//
//        if (badZern)
//          JOptionPane.showMessageDialog(this, "Zernikes without Diameter are ignored.");
//
//    }  //------end of parse()-----------------






//    //-----------public functions for AutoAdjust------------
//    //-----Now that Adjustment is a public class,
//    //-----cannot Auto get its own data?----------------
//    //-----Nope. ArrayList adjustments is private.----------
//    //
//    //---Yikes, sometimes at startup adjustables is all -1 even with good adjustables.
//    //-----What should initialize adjustables??
//
//
//    public double getOsize()
//    // called ONLY by DMF, in support of its static method.
//    {
//        return dOsize;
//    }

    public double getOsize() {
        return parser().getOsize();
    }

//
//    public double getAdjValue(int i)
//    // Fetch appropriate value from RT13.surfs[][].
//    // Adjustables was parsed back in line 318.
//    {
//       if ((adjustables!=null) && (i>=0) && (i<adjustables.size()))
//       {
//           int jsurf = adjustables.get(i).getRecord();
//           int iattr = adjustables.get(i).getAttrib();
//           if ((jsurf>0) && (jsurf<=nsurfs) && (iattr>=0) && (iattr<OFINALADJ))
//             return RT13.surfs[jsurf][iattr];
//       }
//       return 0.0;
//    }
//
    public double getAdjValue(int i) {
        return parser().getAdjValue(i);
    }


//    public int getAdjAttrib(int i)
//    // Adjustables was parsed back in line 318.
//    {
//       if ((adjustables != null) && (i>=0) && (i < adjustables.size()))
//         return adjustables.get(i).getAttrib();
//       else
//         return -1;
//    }

    public int getAdjAttrib(int i) {
        return parser().getAdjAttrib(i);
    }

//
//    public int getAdjSurf(int i)
//    // Adjustables was parsed back in line 318.
//    {
//       if ((adjustables != null) && (i>=0) && (i < adjustables.size()))
//         return adjustables.get(i).getRecord();
//       else
//         return -1;
//    }

    public int getAdjSurf(int i) {
        return parser().getAdjSurf(i);
    }

//
//    public int getAdjField(int i)
//    // Adjustables was parsed back in line 318.
//    {
//       if ((adjustables != null) && (i>=0) && (i < adjustables.size()))
//         return adjustables.get(i).getField();
//       else
//         return -1;
//    }
//

    public int getAdjField(int i) {
        return parser().getAdjField(i);
    }

//    public ArrayList<Integer> getSlaves(int i)
//    // Adjustables was parsed back in line 318.
//    {
//       if ((adjustables != null) && (i>=0) && (i < adjustables.size()))
//         return adjustables.get(i).getList();
//       else
//         return null;
//    }
//
//
    public ArrayList<Integer> getSlaves(int i) {
        return parser().getSlaves(i);
    }


//
//
//
//
//
//    //-------------private stuff----------------
//
//    private static char    cTags[][] = new char[JMAX][MAXFIELDS];
//    private static char    typetag[] = new char[JMAX];
//    private static String  headers[] = new String[MAXFIELDS];
//    private ArrayList<Adjustment>  adjustables;
//    private static int nsurfs=0, nfields=0;
//    private static double dOsize = 0.0;
//
//
//    private int iParseAdjustables(int nsurfs)
//    // fills in private ArrayList of adjustables, with slaves.
//    // Returns how many groups were found based on tags.
//    {
//        boolean bLookedAt[] = new boolean[nsurfs+1];
//        adjustables.clear();
//        for (int field=0; field<nfields; field++)
//        {
//            int attrib = oF2I[field];
//            if ((attrib<0) || (attrib>OFINALADJ))  // or other validity test
//              continue;
//
//            for (int record=1; record<=nsurfs; record++)
//              bLookedAt[record] = false;
//
//            for (int record=1; record<=nsurfs; record++)
//            {
//                char tag0 = getTag(field, record+2);
//                boolean bAdj = isAdjustableTag(tag0);
//                if (!bAdj || bLookedAt[record])
//                {
//                    bLookedAt[record] = true;
//                    continue;
//                }
//
//                //---New adjustable parameter found------------
//                bLookedAt[record] = true;
//                ArrayList<Integer> slaves = new ArrayList<Integer>();
//
//                if (Character.isLetter(tag0))
//                {
//                    boolean bUpper0 = Character.isUpperCase(tag0);
//                    char tag0up = Character.toUpperCase(tag0);
//                    for (int k=record+1; k<=nsurfs; k++)
//                    {
//                        if (!bLookedAt[k])  // find slaves & antislaves
//                        {
//                            char tagk = getTag(field, k+2);
//                            boolean bUpperk = Character.isUpperCase(tagk);
//                            char tagkup = Character.toUpperCase(tagk);
//                            boolean bSameGroup = (tag0up == tagkup);
//                            if (bSameGroup)
//                            {
//                                int iSlave = (bUpper0 == bUpperk) ? k : -k;
//                                slaves.add(new Integer(iSlave));
//                                bLookedAt[k] = true;
//                            }
//                        }
//                    }
//                }
//                adjustables.add(new Adjustment(attrib, record, field, slaves));
//            } // done with all groups in this field
//        }// done with all fields
//        return adjustables.size();
//    }
//
//
//
//    boolean isAdjustable(int jsurf, int iatt)
//    // Tests for range of adjustable attributes & tag chars.
//    // Assumes that oI2F[] and cTags[][] are properly set.
//    // HOWEVER THIS IS DEAF TO THE NEW GANGED PARADIGM.
//    {
//       if ((iatt < 0) || (iatt > OFINALADJ))
//         return false;
//       int field = oI2F[iatt];
//       if ((field < 0) || (field >= nfields))
//         return false;
//       char c = getTag(field, jsurf+2);  // cTags[jsurf][field];
//       return isAdjustableTag(c);
//    }
//
//
//    boolean isAdjustableTag(char c)
//    {
//        return (c=='?') || Character.isLetter(c);
//    }
//
//
//    public static int getSimplifiedOptFieldAttrib(String s)
//    // Given a one character hint at an optics table column header field,
//    // this routine returns the number 0..99 for identified optics table fields,
//    // or else returns -1 indicating ABSENT.
//    //
//    // OSHAPE is absent here since it needs conversion to OASPHER in OEJIF.
//    //
//    // Called by MPlotPanel and MapPanel for their simple optics parameters.
//    {
//        int result = ABSENT;
//        s = s.toUpperCase();
//        if (s.length() < 1)
//          return result;
//        char c = s.charAt(0);
//        switch (c)
//        {
//            case 'A':  result = OASPHER; break;
//            case 'C':  result = OCURVE; break;
//            case 'P':  result = OPITCH; break;
//            case 'R':  result = OROLL; break;
//            case 'T':  result = OTILT; break;
//            case 'X':  result = OX; break;
//            case 'Y':  result = OY; break;
//            case 'Z':  result = OZ; break;
//            default:   result = ABSENT;
//        }
//        return result;
//    }
    
}

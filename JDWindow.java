
// java floating docking windows
// author programania.com

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

class JDWindow extends JDialog { // Прилипающие окна
    static int w=4,ht=18,wx=25; // ширина рамки и высота заголовка, ширина кнопки x
    static Cursor cNResize=Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
    static Cursor cEResize=Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
    static Cursor cMove   =Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    static Cursor cHand   =Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    static Window win;
    static MoveAdapter moveAdapter;
    Rectangle r;
    static Rectangle g; // границы прилипания
    static Color cb=new Color(0xC0D8FF);  // базовый цвет
    static Color ct=new Color(0xFFFFFF);  // цвет текста заголовка
    static Color cf=new Color(0x80B0FF);  // цвет фона заголовка
    static Color cr=cb;                   // цвет рамки
    static Color cX=new Color(0x88AAFF);  // цвет X
    static Color cfx=new Color(0xFFFFFF); // цвет фона X
    static Font  ft=new java.awt.Font("Calibri", Font.PLAIN, 13); // шрифт заголовка

    static JDWindow[] mf=new JDWindow[8]; // для прилипания к другим окнам
    static int qf=0;

    mCanvas p,px,pt,pl,pr,pn; // элементы оформления: заголовок кнопка и рамки
    Component c;
    String title="";

    private boolean dragging=false,sx,sy;

    JDWindow(Component owner) {
      super((JFrame)owner, false);
      setUndecorated(true);
      setLayout(null);
      p =el('z');
      px=el('x');
      pr=el('r');
      pn=el('n');
      pl=el('l');
      pt=el('t');
      if (qf==0) UIManager.getDefaults().put("ScrollBar.width", MSB.h);
      mf[qf++]=this;
    }

    mCanvas el(char t) {
      mCanvas c=new mCanvas(t);
      c.setBackground(t=='z'?cf: t=='x'?cfx:cr);
      c.setVisible(true);
      getContentPane().add(c);
      moveAdapter=new MoveAdapter(t,this);
      c.addMouseListener(moveAdapter);
      c.addMouseMotionListener(moveAdapter);
      if (t=='r'||t=='l') c.setCursor(cEResize); else
      if (t=='n'||t=='t') c.setCursor(cNResize); else
      if (t=='z')         c.setCursor(cMove);    else
      if (t=='x')         c.setCursor(cHand);
      return c;
    }

    public void setTitle(String s) {title=s;}

    void ac(Component C) {
      c=C;
      r=getBounds();
      getContentPane().add(c);
      setB();
    }

    public void setB() {
      if (c!=null) {c.setBounds(w,ht+w, r.width-w*2+1, r.height-ht-w*2); c.repaint();}
      p .setBounds(w,w,    r.width-w*2-wx,ht);
      px.setBounds(r.width-wx-w,w,  wx,ht);
      pr.setBounds(r.width-w,0, w,r.height);
      pl.setBounds(0,0, w,r.height);
      pt.setBounds(w,0, r.width-w*2,w);
      pn.setBounds(w,r.height-w, r.width-w*2,w);
    }

    public void setBounds(int x, int y, int width, int height) {
      super.setBounds(x,y,width,height);
      r=getBounds();
      setB();
    }

    private class mCanvas extends Canvas {
       char t;
       mCanvas(char T) {t=T;}
       public void paint(Graphics g) {
         if (!dragging) {
           ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
           if (t=='z') {
             g.setColor(ct);
             g.setFont(ft);
             g.drawString(title,5,13);
           }
           if (t=='x') {// рисование крестика
             g.setColor(cX);
             ((Graphics2D) g).setStroke(new BasicStroke(2.5f));
             g.drawLine(0,0,  wx-1,ht-1);
             g.drawLine(0,ht-1, wx-1,0);
             ((Graphics2D) g).setStroke(new BasicStroke(1f));
             g.drawRect(0,0,wx-1,ht-1);

           }
         }
       }
    }

  class MoveAdapter extends MouseAdapter {
    private int prevX = -1;
    private int prevY = -1;
    private char t;
    private JDWindow f;

    MoveAdapter(char T, JDWindow F) {t=T; f=F;}

    public void mousePressed(MouseEvent e) {
      if (SwingUtilities.isLeftMouseButton(e)) dragging=true;
      prevX=e.getXOnScreen();
      prevY=e.getYOnScreen();
      win=SwingUtilities.getWindowAncestor(e.getComponent());
      if (win!=null) {
        r=win.getBounds();
        char t;
        try{t=((mCanvas)e.getSource()).t;}catch (Exception x){t=' ';}
        if (t=='x') { // на X
          WindowListener[] wl=win.getWindowListeners();
          if (wl.length>1) wl[0].windowClosing(null);
          else ((JDialog)win).setVisible(false);
        }
      }
    }

    public void mouseDragged(MouseEvent e) {
      int xe=e.getXOnScreen();
      int ye=e.getYOnScreen();
      int x=prevX,y=prevY;

      if (x!=-1 && y!=-1 && dragging && win!=null) {
        if (t=='z') r=new Rectangle(r.x+xe-x, r.y+ye-y, r.width,r.height);  else
        if (t=='r') r=new Rectangle(r.x, r.y, r.width+xe-x,r.height);       else
        if (t=='l') r=new Rectangle(r.x+xe-x, r.y, r.width-(xe-x), r.height); else
        if (t=='n') r=new Rectangle(r.x, r.y, r.width,r.height+ye-y);       else
        if (t=='t') r=new Rectangle(r.x, r.y+ye-y, r.width,r.height-(ye-y));
        win.setBounds(r);
        if (t!='z') setB();
      }
      prevX=xe;
      prevY=ye;
    }

    public void mouseReleased(MouseEvent e) {
      dragging=false;
      Rectangle r=getBounds(), ri;
      int x2=r.x+r.width, y2=r.y+r.height,x3, x2i,y2i;
      boolean l=false,lx,ly;
      int dy=y2-(g.y+g.height);
      int dx=x2-(g.x+g.width);
// прилипание к границам g
      if (a(r.x-g.x)<32) {r.x=g.x; l=true;}
      if (a(r.y-g.y)<32) {r.y=g.y; l=true;}
      if (a(dy)     <32) {r.y-=dy; l=true;}
      if (a(dx)     <32) {r.x-=dx; l=true;}
      x2=r.x+r.width;
      y2=r.y+r.height;
// прилипание других
      for (int i=qf;i-->0;) {
        if (mf[i].equals(f)) continue;
        lx=ly=false;
        ri=mf[i].getBounds();
        x2i=ri.x+ri.width;
        y2i=ri.y+ri.height;
        x3=x2i-r.x;
        sy=ri.y<=y2 && y2i>=r.y; // перекрытие по вертикали
        sx=ri.x<=x2 && x2i>=r.x; // перекрытие по горизонтали

        if (sy && a(ri.x-x2)<32) {lx=true; ri.width+=ri.x-x2; ri.x=x2;}
        if (sy && a(x3)<32)      {lx=true; ri.width-=x3;}
        if (sx && a(ri.y-y2)<32) {ly=true; ri.height+=ri.y-y2; ri.y=y2;}

        if (lx && a(r.height-ri.height)<32 && a(ri.y-r.y)<32) {ri.y=r.y; ri.height=r.height;}
        if (ly && a(r.width -ri.width) <32 && a(ri.x-r.x)<32) {ri.x=r.x; ri.width =r.width;}

        if (lx || ly) {mf[i].setBounds(ri); mf[i].r=ri; mf[i].setB();}
      }

      if (l) setBounds(r);
      p.repaint();
      px.repaint();
    }

  }
  static int a(int i) {return Math.abs(i);}

  static JDWindow showWin(JDWindow f, Component owner, JScrollPane sp, JEditorPane ep, Rectangle r, String t, String s) {
// показ на форме html
    if (f==null) {
      f=new JDWindow(owner);
      f.setTitle(t);
      if (ep==null) ep =new JEditorPane();
      if (sp==null) sp =new JScrollPane();
      sp.setFocusable(false);
      setHtml(sp,ep);
      f.ac(sp);
      f.setBounds(r);
    }
    ep.setText(s);
    f.setVisible(true);
    return f;
  }

  static void setHtml(JScrollPane jSP, JEditorPane hv) { // создание всего нужного для показа html в окне
    hv.setContentType("text/html");
    hv.setEditable(false);
    jSP.getViewport().add(hv, null);
    jSP.getVerticalScrollBar  ().setUI(new MSB());
    jSP.getHorizontalScrollBar().setUI(new MSB());
  }

}

class MSB extends BasicScrollBarUI {     // свой ScrollBar
  static Color c=new Color(150,190,225); // фон
  static Color d=new Color(228,230,240); // движок
  static Color D=new Color(255,200,180); // движок движимый
  static Color z;         // текущая заливка
  static Graphics2D g;
  static Image iv,ih, dv,dh, nv,nh, b;   // картинки для фона и движка не нажатого и нажатого и кнопок
  static boolean v;                      // вертикальная
  static boolean ini=true;               // выполнить ini
  static int we,he;                      // размеры экрана
  static int h=13;                       // ширина ScrollBar

  static Color ic(Color c, int i)  { // изменение яркости на величину i
    int r=c.getRed()  +i; r=r>255? 255: r<0? 0:r;
    int g=c.getGreen()+i; g=g>255? 255: g<0? 0:g;
    int b=c.getBlue() +i; b=b>255? 255: b<0? 0:b;
    return new Color(r,g,b);
  }

  static void fill(int y1, int y2, int d1, int d2) {
    if (v) {
      g.setPaint(new GradientPaint(y1,0,ic(z,d1), y2,0,ic(z,d2)));
      g.fillRect(y1, 0, y2, he);
    }
    else {
      g.setPaint(new GradientPaint(0,y1,ic(z,d1), 0,y2,ic(z,d2)));
      g.fillRect(0, y1, we, y2);
    }
  }
  static void drawI(Image i) {// фон
    g=(Graphics2D)i.getGraphics();
    z=c;
    fill(0,    h/6,   -5, -8);
    fill(h/6,  h/2,   -8,  5);
    fill(h/2,  h*5/6,  5,  8);
    fill(h*5/6,h+1,    8,-16);
  }

  static void drawD(Image i, int c1, int c2, Color d) {// движок
    g=(Graphics2D)i.getGraphics();
    z=d;
    fill(0, h, c1, c2);
  }

  static void getWH() {// присвоение размеров экрана
    Dimension size=Toolkit.getDefaultToolkit().getScreenSize();
    we=(int)size.getWidth();
    he=(int)size.getHeight();
  }

  static void ini() {
    getWH();
    ini=false;
    iv=new BufferedImage(h+1,h+1,BufferedImage.TYPE_INT_RGB);
    ih=new BufferedImage(h+1,h+1,BufferedImage.TYPE_INT_RGB);
    dv=new BufferedImage(h,h,    BufferedImage.TYPE_INT_RGB);
    dh=new BufferedImage(h,h,    BufferedImage.TYPE_INT_RGB);
    nv=new BufferedImage(h,h,    BufferedImage.TYPE_INT_RGB);
    nh=new BufferedImage(h,h,    BufferedImage.TYPE_INT_RGB);

    v=true;  drawI(iv); drawD(dv,10,-30, d); drawD(nv,-20,10, D);
    v=false; drawI(ih); drawD(dh,10,-30, d); drawD(nh,-20,10, D);
    b=BAB.imi(d, 5, -25, h - 2); // кнопки
  }

  protected JButton createDecreaseButton(int orientation) {return new BAB(orientation);}
  protected JButton createIncreaseButton(int orientation) {return new BAB(orientation);}

  protected void paintTrack(Graphics g, JComponent cm, Rectangle r) {
    if (ini) ini();
    int x=r.x, y=r.y, w=r.width, h=r.height;
    v=((JScrollBar)cm).getOrientation()==JScrollBar.VERTICAL;
    g.drawImage(v? iv:ih, x,y,w,h,c,null);
  }

  protected void paintThumb(Graphics g, JComponent cm, Rectangle r) {
    if (ini) ini();
    int x=r.x+1, y=r.y+1, w=r.width-2, h=r.height-2;
    v=((JScrollBar)cm).getOrientation()==JScrollBar.VERTICAL;
    Color c=isDragging? D:d;
    g.drawImage(isDragging? v?nv:nh : v?dv:dh, x-1,y,w+1,h+(v?0:1), null);
// рамка
    g.setColor(ic(c,-40));
    g.drawRect(x-1,y-1, w+1,h+1);
//полоски
    if (v) {int yy=y,hh=h; h=w; y=x; x=yy; w=hh; if (hh<18) return;}
    int nn=x+(w-12)/2+1, y1=y+4, y2=y+h-4;
    g.setColor(ic(c,-60));
    for (int j=0;j<2;j++) {
      for (int i=0,n=nn; i<4; i++,n+=3) if (v) g.drawLine(y1,n,y2,n); else g.drawLine(n,y1,n,y2);
      g.setColor(ic(c,45));
      y1++; y2++; nn++;
    }
  }
}

class BAB extends JButton implements SwingConstants { // своя BasicArrowButton
  boolean mouse=false;     // мышь попала на кнопку - выделить
  protected int direction;

  static Image iw;
  static Color cT=new Color(0);        // цвет значка
  static Color cr=new Color(128,128,128); //цвет рамки


  public BAB(int direction) {
    super();
    setRequestFocusEnabled(false);
    setDirection(direction);
  }

  public void setDirection(int dir) {direction=dir;}

  public void paint(Graphics g) {
    if (MSB.ini) MSB.ini();
    int w=getWidth(), h=getHeight(), size;
    boolean v=direction==NORTH || direction==SOUTH;
    g.setColor(MSB.c);
    g.fillRect(0,0,w,h);
    g.drawImage(mouse? iw : MSB.b, 1,1, w-2,h-2,null);
// рамка
    g.setColor(mouse? cr : MSB.ic(MSB.d,-55));
    g.drawRoundRect(0,0,w-1,h-1,4,4);
// стрелка
    size=Math.min((h-4)/3, (w-4)/3);
    size=Math.max(size, 2);

    new BasicArrowButton(direction,null,null,
        mouse? cT :MSB.ic(MSB.c,-40),null).paintTriangle(g,
        (w-size)/2+(v?2:0)+(direction==EAST? 1:0),
        (h-size)/2+(v?0:1)+(direction==SOUTH?1:0),
        size, direction, true);
    mouse=false;
  }

  static Image imi(Color c, int d1, int d2, int h) {// картинка с градиентом
    Image i=new BufferedImage(h,h,BufferedImage.TYPE_INT_RGB);
    Graphics2D g=(Graphics2D)i.getGraphics();
    g.setPaint(new GradientPaint(0, 0, MSB.ic(c,d1), 0, h, MSB.ic(c,d2)));
    g.fillRect(0, 0, h, h);
    return i;
  }

  public Dimension getPreferredSize() {return new Dimension(MSB.h,MSB.h);}
}


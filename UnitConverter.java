import javax.swing.*; 
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*; import java.awt.event.*;

public class UnitConverter {
    static final Color BG=new Color(0x141414),CB=new Color(0x1E1E1E),
        FB=new Color(0x252525),AC=new Color(0xF5A623),
        TX=new Color(0xECECEC),ST=new Color(0x888888),BR=new Color(0x2E2E2E);
    static final Font TF=new Font("SansSerif",1,22),LF=new Font("SansSerif",0,12),
        FF=new Font("Monospaced",0,14),CF=new Font("SansSerif",1,15);

    public static void main(String[] a){SwingUtilities.invokeLater(UnitConverter::menu);}

    static void menu(){
        JFrame f=frame("Unit Converter",560,580);
        JPanel r=pan(new BorderLayout());f.setContentPane(r);
        r.add(lbl("Unit Converter",TF,TX,new EmptyBorder(28,0,8,0)),BorderLayout.NORTH);
        JPanel g=pan(new GridLayout(3,3,14,14));g.setBorder(new EmptyBorder(16,26,30,26));
        String[][]cd={{"[L]","Length"},{"[T]","Temp"},{"[>]","Speed"},
                       {"01","Number"},{"[]2","Area"},{"[]3","Volume"},
                       {"W","Power"},{"kg","Weight"},{"",""}};
        for(String[]c:cd){if(c[1].isEmpty())g.add(pan(new BorderLayout()));else g.add(card(c[0],c[1]));}
        r.add(g,BorderLayout.CENTER);f.setVisible(true);
    }

    static JPanel card(String icon,String name){
        JPanel p=new JPanel(new BorderLayout(0,6)){
            boolean h=false;
            {setOpaque(false);setCursor(Cursor.getPredefinedCursor(12));
             addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){h=true;repaint();}
                public void mouseExited(MouseEvent e){h=false;repaint();}
                public void mouseClicked(MouseEvent e){open(name);}
             });}
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(h?new Color(0x2A2A2A):CB);g2.fillRoundRect(0,0,getWidth(),getHeight(),18,18);
                if(h){g2.setColor(AC);g2.setStroke(new BasicStroke(1.5f));g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,18,18);}
                g2.dispose();super.paintComponent(g);}};
        p.setBorder(new EmptyBorder(16,8,16,8));
        p.add(lbl(icon,new Font("SansSerif",0,22),AC,null),BorderLayout.CENTER);
        p.add(lbl(name,CF,TX,null),BorderLayout.SOUTH);return p;
    }

    static void open(String cat){
        switch(cat){
            case"Length":showC("Length",
                new String[]{"Angstrom(\u00C5)","Nano(nm)","Micro(\u00B5m)","Milli(mm)","Centi(cm)","Metre(m)","Kilo(km)"},
                new double[]{1e-10,1e-9,1e-6,1e-3,1e-2,1,1e3});break;
            case"Speed":showC("Speed",
                new String[]{"m/s","km/h","mph","knot"},
                new double[]{1,1.0/3.6,0.44704,0.514444});break;
            case"Area":showC("Area",
                new String[]{"mm\u00B2","cm\u00B2","m\u00B2","km\u00B2","in\u00B2","ft\u00B2"},
                new double[]{1e-6,1e-4,1,1e6,6.4516e-4,0.092903});break;
            case"Volume":showC("Volume",
                new String[]{"mL","cL","dL","L","m\u00B3","gal","qt","pt"},
                new double[]{1e-3,0.01,0.1,1,1000,3.78541,0.946353,0.473176});break;
            case"Power":showC("Power",
                new String[]{"Watt(W)","Kilowatt(kW)","Megawatt(MW)","Horsepower(hp)","BTU/hr","kcal/hr"},
                new double[]{1,1000,1e6,745.7,0.29307,1.163});break;
            case"Weight":showC("Weight",
                new String[]{"Milligram(mg)","Gram(g)","Kilogram(kg)","Tonne(t)","Pound(lb)","Ounce(oz)","Stone"},
                new double[]{1e-6,1e-3,1,1000,0.453592,0.0283495,6.35029});break;
            case"Temp":showTemp();break;
            case"Number":showNum();break;
        }
    }

    static void showC(String title,String[]names,double[]fac){
        JFrame f=frame(title,460,Math.min(80+names.length*72,620));
        JPanel root=new JPanel();root.setLayout(new BoxLayout(root,BoxLayout.Y_AXIS));
        root.setBackground(BG);root.setBorder(new EmptyBorder(8,28,16,28));
        JTextField[]fs=new JTextField[names.length];boolean[]upd={false};
        for(int i=0;i<names.length;i++){
            final int x=i;fs[i]=field(fs,upd);root.add(row(names[i],fs[i]));
            fs[i].getDocument().addDocumentListener(new DocumentListener(){
                void go(DocumentEvent e){
                    if(upd[0])return;upd[0]=true;
                    String t=fs[x].getText().trim();
                    if(t.isEmpty()){for(int j=0;j<fs.length;j++)if(j!=x)fs[j].setText("");}
                    else try{double v=Double.parseDouble(t),base=v*fac[x];
                        for(int j=0;j<fs.length;j++)if(j!=x)fs[j].setText(sFmt(base/fac[j]));
                    }catch(Exception ex){for(int j=0;j<fs.length;j++)if(j!=x)fs[j].setText("");}
                    upd[0]=false;}
                public void insertUpdate(DocumentEvent e){go(e);}
                public void removeUpdate(DocumentEvent e){go(e);}
                public void changedUpdate(DocumentEvent e){}
            });
        }
        JPanel wrap=pan(new BorderLayout());
        wrap.add(lbl(title+" Converter",TF,TX,new EmptyBorder(20,0,12,0)),BorderLayout.NORTH);
        wrap.add(root,BorderLayout.CENTER);
        JScrollPane sp=new JScrollPane(wrap);sp.setBorder(null);
        sp.getViewport().setBackground(BG);sp.getVerticalScrollBar().setUnitIncrement(12);
        f.setContentPane(sp);f.setVisible(true);
    }

    static void showTemp(){
        String[]n={"Celsius(\u00B0C)","Fahrenheit(\u00B0F)","Kelvin(K)"};
        JFrame f=frame("Temperature",460,310);
        JPanel root=new JPanel();root.setLayout(new BoxLayout(root,BoxLayout.Y_AXIS));
        root.setBackground(BG);root.setBorder(new EmptyBorder(8,28,16,28));
        JTextField[]fs=new JTextField[3];boolean[]upd={false};
        for(int i=0;i<3;i++){
            final int x=i;fs[i]=field(fs,upd);root.add(row(n[i],fs[i]));
            fs[i].getDocument().addDocumentListener(new DocumentListener(){
                void go(DocumentEvent e){
                    if(upd[0])return;upd[0]=true;
                    String t=fs[x].getText().trim();
                    if(t.isEmpty()){for(int j=0;j<3;j++)if(j!=x)fs[j].setText("");}
                    else try{double v=Double.parseDouble(t),c=x==0?v:x==1?(v-32)*5/9.0:v-273.15;
                        for(int j=0;j<3;j++)if(j!=x)fs[j].setText(pFmt(j==0?c:j==1?c*9/5.0+32:c+273.15));
                    }catch(Exception ex){for(int j=0;j<3;j++)if(j!=x)fs[j].setText("");}
                    upd[0]=false;}
                public void insertUpdate(DocumentEvent e){go(e);}
                public void removeUpdate(DocumentEvent e){go(e);}
                public void changedUpdate(DocumentEvent e){}
            });
        }
        JPanel w=pan(new BorderLayout());
        w.add(lbl("Temperature Converter",TF,TX,new EmptyBorder(20,0,12,0)),BorderLayout.NORTH);
        w.add(root,BorderLayout.CENTER);f.setContentPane(w);f.setVisible(true);
    }

    static void showNum(){
        String[]n={"Decimal","Binary","Hexadecimal","Octal"};int[]r={10,2,16,8};
        JFrame f=frame("Number System",460,390);
        JPanel root=new JPanel();root.setLayout(new BoxLayout(root,BoxLayout.Y_AXIS));
        root.setBackground(BG);root.setBorder(new EmptyBorder(8,28,16,28));
        JTextField[]fs=new JTextField[4];boolean[]upd={false};
        for(int i=0;i<4;i++){
            final int x=i;fs[i]=field(fs,upd);
            if(x==1)((AbstractDocument)fs[1].getDocument()).setDocumentFilter(new DocumentFilter(){
                public void insertString(FilterBypass b,int o,String s,AttributeSet a)throws BadLocationException{if(s.matches("[01]+"))super.insertString(b,o,s,a);}
                public void replace(FilterBypass b,int o,int l,String s,AttributeSet a)throws BadLocationException{if(s.matches("[01]*"))super.replace(b,o,l,s,a);}
            });
            root.add(row(n[i],fs[i]));
            fs[i].getDocument().addDocumentListener(new DocumentListener(){
                void go(DocumentEvent e){
                    if(upd[0])return;upd[0]=true;
                    String t=fs[x].getText().trim();
                    if(t.isEmpty()){for(int j=0;j<4;j++)if(j!=x)fs[j].setText("");}
                    else try{long v=Long.parseLong(t,r[x]);
                        for(int j=0;j<4;j++)if(j!=x)fs[j].setText(Long.toString(v,r[j]).toUpperCase());
                    }catch(Exception ex){for(int j=0;j<4;j++)if(j!=x)fs[j].setText("");}
                    upd[0]=false;}
                public void insertUpdate(DocumentEvent e){go(e);}
                public void removeUpdate(DocumentEvent e){go(e);}
                public void changedUpdate(DocumentEvent e){}
            });
        }
        JPanel w=pan(new BorderLayout());
        w.add(lbl("Number System Converter",TF,TX,new EmptyBorder(20,0,12,0)),BorderLayout.NORTH);
        w.add(root,BorderLayout.CENTER);f.setContentPane(w);f.setVisible(true);
    }

    static JFrame frame(String t,int w,int h){
        JFrame f=new JFrame(t);f.setSize(w,h);f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);f.setResizable(false);return f;}

    static JPanel pan(LayoutManager l){JPanel p=new JPanel(l);p.setBackground(BG);return p;}

    static JLabel lbl(String t,Font f,Color c,Border b){
        JLabel l=new JLabel(t,SwingConstants.CENTER);l.setFont(f);l.setForeground(c);
        if(b!=null)l.setBorder(b);return l;}

    static JPanel row(String name,JTextField tf){
        JPanel p=pan(new BorderLayout(0,4));p.setBorder(new EmptyBorder(5,0,5,0));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE,70));
        JLabel lb=new JLabel(name);lb.setFont(LF);lb.setForeground(ST);
        p.add(lb,BorderLayout.NORTH);p.add(tf,BorderLayout.CENTER);return p;}

    static JTextField field(JTextField[]all,boolean[]upd){
        JTextField tf=new JTextField(){
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FB);g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);g2.dispose();
                super.paintComponent(g);}};
        tf.setOpaque(false);tf.setForeground(TX);tf.setCaretColor(AC);tf.setFont(FF);
        tf.setBorder(new CompoundBorder(new AbstractBorder(){
            public void paintBorder(Component c,Graphics g,int x,int y,int w,int h){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BR);g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(x,y,w-1,h-1,12,12);g2.dispose();}},
            new EmptyBorder(8,12,8,12)));
        tf.setPreferredSize(new Dimension(0,42));
        // ESC clears all fields
        tf.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
                    upd[0]=true;for(JTextField t:all)t.setText("");upd[0]=false;}}});
        return tf;}

    static String sFmt(double v){
        if(!Double.isFinite(v))return"N/A";if(v==0)return"0";
        double a=Math.abs(v);
        if(a>=0.001&&a<1e6)return strip(String.format("%.6g",v));
        int e=(int)Math.floor(Math.log10(a));
        return strip(String.format("%.4f",v/Math.pow(10,e)))+" x 10^"+e;}

    static String pFmt(double v){
        if(!Double.isFinite(v))return"N/A";
        if(v==Math.floor(v)&&Math.abs(v)<1e12)return String.valueOf((long)v);
        return strip(String.format("%.6f",v));}

    static String strip(String s){
        if(s.contains(".")){s=s.replaceAll("0+$","");s=s.replaceAll("\\.$","");}return s;}
}
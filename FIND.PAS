Uses Crt{,dos};
Const
  minyan:array [1..38] of byte=(50,40,27,36,34,24,21,31,24,22,25,66,52,48,14,4,9,1,4,7,3,3,3,2,14,3,150,31,42,8,4,5,
    12,10,12,10,13,29);
  nums:array [1..9] of integer=(3814,5085,11440,12711,15253,16254,17795,21608,27963);
  engleter:string[28]=' TCDSVUZJYHFKNBXGPMERA,L;.OI';
  hebleter:string[28]=' €‚ƒ„…†‡ˆ‰‹Œ‘’”–—˜™šŠ“•'; // thanks to hebTx.html: const eng = "àáâãäåæçèéëìîğñòôö÷øùúêóõíï";
  Names:Array [1..39] Of String[13]=('úéùàøá','úåîù','àø÷éå','øáãîá','íéøáã','òùåäé','íéèôåù','''à ìàåîù','''á ìàåîù', '''à íéëìî','''á íéëìî','äéòùé','äéîøé','ìà÷æçé','òùåä','ìàåé','ñåîò','äéãáåò','äğåé','äëéî','íåçğ','÷å÷áç','äéğôö', 'éâç','äéøëæ','éëàìî','íéìäú','éìùî','áåéà','íéøéùä øéù','úåø','äëéà','úìä÷','øúñà','ìàéğã','àøæò','äéîçğ', '''à íéîéä éøáã','''á íéîéä éøáã');	
  Posi:Array [1..40] Of Word=(0,1533,2743,3602,4890,5846,6502,7120,7931,8626,9443,10162,11453,12817,14090,14287,14360,14506,
    14527,14575,14680,14727,14783,14836,14874,15085,15140,17667,18582,19652,19769,19854,20008,20230,20397,20754,21034,21439,
    22382,23204);
  ZZ:Array [1..40] Of Integer=(1,2044,3712,4898,6614,8017,9033,10010,11295,12370,13653,14823,16550,18675,20560,20807,20905,
    21110,21140,21210,21350,21408,21480,21559,21617,21931,22015,24568,25485,26583,26722,26851,27048,27335,27628,28222,28633,
    29219,30431,31776);
  oo:array [#1..#2] of string[8]=('" š†…˜‡',' Š˜’');
  lessPsk:array [1..9] of string[45]=('ÿÕL§í>Åÿ@/Ç"»ì üd!HŸ¦gñ|á3‰ıCû%÷é?hÉ÷¸¿',
    'ÿôü‰Öïèù{±ßĞò''X‡ôÿ‰ıbşòÅeş˜½|bÊ{°¿','jHÀ},ù3$ü‹ş|eä¤¿iROŒ²Úv|eôè¸ öÈŸbş ¤¿',
    'ÿÿÿ´‰…>É}¬(ò„øùµ9fKò¯ ùµ9qşLËíHÊéíaAı ¿','ÿÿüúÃŒ_µ0t¡ôÉúd}ëû%ıaføõ NşbıRÊ~’0Ñ×Ÿ¥',
    'ÿÈÃı+ş¶TúJ/…OÚŒàûJş¸Ør¤Ÿ# ¿ú]ı ËüŸ¥ß','`Cõ1ßÅ¬¡‡Æ—íHò²|iôøL‰ıïÔ>˜@Ÿ!HŸáÀù÷­Ÿ',
    'ÿñ}¥I>÷3é’s¦|’ _µ8Ì¾Ò¥ß#$âü†''ÚT“ïwÚ°…Ÿ>W''?','ÿÀ}îû ˜÷‰ğ_{¾KÆgğà}€LÂı$+%ğEŸwŞ‘€?');
Type
  Recor=Record
    Perek,Pasuk:Byte;
    Txt:array [1..45] of byte;
  End;
  u=array [1..23204] of word;
Var
  F,fil:File;
  value:^u;
  psukim,b,g,code,j,m,i,e,StPrk,StPsk,Stop,Place,Size2,X:Byte;
  a,s,w,p,xxx,find,Heb:String[72];
  qw,prakim,k,l,s2,min,max,Count,Size,Demo:Integer;
  Reco:Recor;
  buf:array [1..1271] of recor;
  Fil2:File of recor;
  c:char;
  t:Text;
{  y,y2,y3,y4:word;}

procedure decrypt2;
begin
  m:=1;
  for i:=1 to 72 do begin
    s[i]:=chr(reco.txt[m] shr 3);                                {11111000}
    s[i+1]:=chr(reco.txt[m] and 7 shl 2 or reco.txt[m+1] shr 6); {00000111 + 11000000}
    s[i+2]:=chr(reco.txt[m+1] and 62 shr 1);                                {00111110}
    s[i+3]:=chr(reco.txt[m+1] and 1 shl 4 or reco.txt[m+2] shr 4);          {00000001 + 11110000}
    s[i+4]:=chr(reco.txt[m+2] and 15 shl 1 or reco.txt[m+3] shr 7);                    {00001111 + 10000000}
    s[i+5]:=chr(reco.txt[m+3] and 124 shr 2);                                                     {01111100}
    s[i+6]:=chr(reco.txt[m+3] and 3 shl 3 or reco.txt[m+4] shr 5);                                {00000011 + 11100000}
    s[i+7]:=chr(reco.txt[m+4] and 31);                                                                       {00011111}
    inc(m,5);
    inc(i,7);
  end;
end;

procedure decrypt;
begin
  decrypt2;
  for i:=1 to 72 do
    if s[i]=#31 then
      inc(s[i])
    else
      inc(s[i],128);
end;

procedure show;
begin
  decrypt;
  place:=Pos(find,'  '+s+'  ');
  write(s:80);
  writeln(t,s:79);
  if place>0 then begin
    gotoxy(7+place,whereY-1);
    textattr:=white;
    write(find);
    gotoxy(1,wherey+byte(wherex>6));
    textattr:=green;
  end;
end;

procedure error(s:string);
begin
  textattr:=cyan;
  write(^j,'ş ',s,#7);
  textattr:=lightGray;
  writeln;
  halt;
end;

Procedure Adds(Ch:char;N:Byte);
Begin
  Heb:=Ch+Heb;
  Inc(Code,N);
End;

Procedure FindNo;
Begin
  Code:=31;
  Heb:='';
  Repeat
    If Code+100<=Size2 Then Adds('—',100)
    Else If Code+90<=Size2 Then Adds('–',90)
    Else If Code+80<=Size2 Then Adds('”',80)
    Else If Code+70<=Size2 Then Adds('’',70)
    Else If Code+60<=Size2 Then Adds('‘',60)
    Else If Code+50<=Size2 Then Adds('',50)
    Else If Code+40<=Size2 Then Adds('',40)
    Else If Code+30<=Size2 Then Adds('Œ',30)
    Else If Code+20<=Size2 Then Adds('‹',20)
    Else If size2-code in [15,16] Then Adds('ˆ',9)
    Else For X:=10 DownTo 1 Do
      If Code+X<=Size2 Then
        Adds(Chr(127+X),X);
  Until Code=Size2;
  If Length(Heb)>1 Then
    Insert('"',Heb,2)
  Else
    Heb:=''''+Heb;
End;

procedure writeInfo;
begin
  Inc(Count);
  findno;
  textattr:=magenta;
  Write('(',heb,' ,');
  write(t,'(',heb,' ,');
  size2:=stprk;
  findno;
  WriteLn(heb,' ',Names[e],')'^j);
  writeln(t,heb,' ',names[e],')'^m^j);
end;

Begin
  If LastMode<2 Then
    TextMode(Co80);
  CheckBreak:=False;
  textattr:=lightgreen;
  writeln(^j'ş Œˆ ˜‡™ (c) ''98'^j);
  textattr:=lightblue;
  for x:=1 to 8 do begin
    if x=8 then
      write(' ':18)
    else
      write(names[32+x]:13,32+x:3,' ³');
    write(names[24+x]:11,24+x:3,' ³',names[16+x]:11,16+x:3,' ³',names[8+x]:11,8+x:3,' ³',names[x]:11,x:3);
  end;
  textattr:=lightred;
  WriteLn(^j'ÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ');
  textattr:=lightmagenta;
  Writeln('îçøåæú àå òøê îñôøé ìçéôåù -> ');
  repeat
    gotoxy(31,wherey-1);
    heb:='';
    x:=wherex;
    repeat
      repeat
        c:=upcase(readkey);
        if c=#0 then begin
          c:=readkey;
          c:=#0;
        end;
      until c>#0;
      if c=#27 then begin
        heb:='';
        clreol;
      end else if length(heb)<49 then
        if pos(c,engleter)>0 then
          heb:=hebleter[pos(c,engleter)]+heb
        else if c in ['0'..'9'] then
          heb:=heb+c;
      write(heb);
      gotoxy(x,wherey);
      if c=#8 then begin
        if heb[length(heb)] in ['0'..'9'] then
          dec(heb[0])
        else
          delete(heb,1,1);
        write(heb,' ');
        gotoxy(x,wherey);
      end;
    until c=#13;
    writeln;
  until heb>'';
  w:='';
  Val(Heb,Size,Demo);
  if demo>1 then
    val(copy(heb,1,demo-1),size,demo);
  find:=heb;
  c:=#2;
  If Demo>0 Then begin
    w:=heb;
    For X:=1 To Length(Heb) Do
      Case Heb[x] Of
       '€'..'‰':Inc(Size,Ord(Heb[X])-127);
        'Š','‹':Inc(Size,20);
  	    'Œ':Inc(Size,30);
        '','':Inc(Size,40);
        '','':Inc(Size,50);
	    '‘':Inc(Size,60);
 	    '’':Inc(Size,70);
        '“','”':Inc(Size,80);
        '•','–':Inc(Size,90);
       '—'..'š':Inc(Size,100*(Ord(Heb[X])-150));
      End;
    textattr:=lightGreen;
    write(^j'çéôåù )î(çøåæú àå ôñå÷ ùååä )ò(øê');
    repeat
      c:=upcase(readkey);
    until c in ['N','G','','’'];
    if c in ['N',''] then begin
      c:=#1;
      writeln('š†…˜‡');
      while pos('  ',copy(find,2,length(find)-2))>0 do
        delete(find,pos('  ',copy(find,2,length(find)-2))+1,1);
      size:=0;
{      if find[length(find)] in ['0'..'9'] then
        error('àéï ìçôù ñôøåú');}
      for count:=1 to length(find) do
        if find[count]>='€' then
          inc(size);
      if size=0 then
        error('àéï ìçôù øååçéí')
      else if size=1 then
        error('š‡€ š…€ ™”‡Œ ‰€'); (***)
{      size:=0;
      for count:=1 to length(find) do
        if find[count]=' ' then
          size:=0
        else if size=10 then
          error('‰……š 11- „‹…˜€„ „Œ ™”‡Œ ‰€')
        else
          inc(size);}
    end else begin
      c:=#2;
      writeln('Š˜’ „……™ —…‘”');
    end;
    gotoxy(31,wherey-3);
    clreol;
    textattr:=lightMagenta;
    if c=#2 then
      writeln(heb,^j^j)
    else
      writeln('"',find,'"'^j^j);
  end;
  if (c=#2) and (size<159) or (size>13639) then
    error('159-13639 äòøê àéğå áúçåí');
  writeln(^j);
  textattr:=lightcyan;
  Repeat
    GotoXY(1,WhereY-1);
    Write('˜”‘ ™”‡Œ -> ');
    ClrEol;
    ReadLn(Heb);
    Val(Heb,X,Demo);
    if demo>1 then
      val(copy(heb,1,demo-1),x,demo);
  Until x in [1..39];
  GotoXY(14,WhereY-1);
  Writeln(names[x]);
  ClrEol;
  WriteLn(^j);
  textattr:=yellow;
  Repeat
    GotoXY(1,WhereY-1);
    Write(' åòã ñôø )ëåìì( -> ');
    ClrEol;
    ReadLn(Heb);
    Val(Heb,Stop,Demo);
    if demo>1 then
      val(copy(heb,1,demo-1),stop,demo);
  Until stop in [1..39];
  GotoXY(19,WhereY-1);
  Writeln(names[stop]);
  ClrEol;
  textattr:=red;
  if stop<x then
    error('èååç ìà çå÷é');
  Count:=0;
  Assign(Fil2,'Bible.Txt');
  assign(fil,'bible.txt');
  if c=#2 then begin
    Assign(F,'Bible.Gim');
    {$i-} Reset(F,46408); {$i+}
    if ioresult<>0 then
      error('Bible.Gim š‡‰š” š…’ˆ');
    new(value);
    blockread(f,value^,1);
    {$i-} reset(fil2); {$i+}
  end else
    {$i-} Reset(Fil,59737); {$i+}
  if ioResult<>0 then
    error('Bible.Txt èòåú áôúéçú');
  writeln(^j'š... àğà ä'^j);
  str(x,heb);
  str(stop,xxx);
  str(size,p);
  if c=#2 then
    assign(t,p+'-'+heb+'.'+xxx)
  else
    assign(t,'s-'+heb+'.'+xxx);
  xxx:=names[stop];
  rewrite(t);
  writeln(t);
  s[0]:=#71;
  e:=1;
 {  gettime(y,y2,y3,y4);
  settime(y,y2,0,0);}
  if c=#2 then begin
    prakim:=31;
    For Demo:=1 To 23204 Do begin
      if value^[demo]>16000 then begin
        psukim:=32;
        dec(value^[demo],16000);
        inc(prakim);
      end else
        inc(psukim);
      If (value^[demo]=Size) and (demo>posi[x]) and (demo<=posi[stop+1]) Then Begin
        while demo>posi[e+1] do
          inc(e);
        textAttr:=green;
        min:=ZZ[e]-1;
        max:=zz[e+1]-2;
        stprk:=prakim;
        for size2:=1 to e-1 do
          dec(stprk,minyan[size2]);
        size2:=psukim;
        repeat
          seek(fil2,(min+max) shr 1);
          read(fil2,reco);
          if (reco.perek>stprk) or (reco.perek=stprk) and (reco.pasuk>size2) then
            max:=(min+max) shr 1
          else
            min:=(min+max) shr 1;
        until (reco.pasuk=size2) and (reco.perek=stprk);
        if filepos(fil2)>3 then begin
          seek(fil2,filepos(fil2)-4);
          repeat
            read(fil2,reco);
          until (reco.pasuk=size2) and (reco.perek=stprk);
        end;
        repeat
          decrypt;
          write(s:80);
          writeln(t,s:79);
          reco.pasuk:=253;
          if not eof(fil2) then
	    read(fil2,reco);
        until reco.pasuk<>size2;
        writeInfo;
      End
    end
  end else begin
    seek(fil,(zz[x]-1) div 1271);
    l:=(filePOS(fil)-1)*1271;
    lessPsk[3,36]:=#9;
    lessPsk[3,43]:=#0;
    lessPsk[4,44]:=#0;
    lessPsk[4,25]:=#0;
    lessPsk[5,29]:=#26;
    lessPsk[6,32]:=#0;
    lessPsk[6,39]:=#0;
    lesspsk[8,17]:=#0;
    lessPsk[9,7]:=#0;
    a:=find;
    for i:=1 to length(find) do
      if a[i]=#32 then
        a[i]:=#31
      else
        dec(a[i],128);
    b:=0;
    k:=1;
    blockRead(fil,buf,1);
    for j:=(zz[stop+1]-zz[x]+1271) div 1271 downto 0 do begin
      inc(l,1271);
      for k:=k to 1271 do begin
	reco:=buf[k];
	decrypt2;
        qw:=1;
        while s[qw]=#31 do inc(qw);
        if (s[71]=chr(ord('‹')-128)) and (s[qw]=chr(ord('Œ')-128)) then begin
{	If Pos(a,''+s+'')>0 Then Begin} {*****}
	  while l+k>=zz[e+1] do
	    inc(e);
          if e in [x..stop] then begin
            stprk:=reco.perek;
            size2:=reco.pasuk;
            while (buf[k].pasuk=size2) and (buf[k].perek=stprk) and (k>0) do
              dec(k);
            inc(k);
            textattr:=green;
            if k=1 then begin            {ä÷åã}
              for k:=1 to 9 do
                if nums[k]=l+1 then begin
                  for g:=1 to 45 do
                    reco.txt[g]:=byte(lesspsk[k,g]);
                  show;
                  k:=9;
                end;
              k:=1;
            end;
	    repeat
              reco:=buf[k];
              show;
              inc(k);
            until (buf[k].pasuk<>size2) or (k=1272);
            if k<>1272 then
              dec(k)
            else begin
              for k:=1 to 9 do
                if nums[k]=l+1272 then     {€„}
                  b:=k;
              k:=1271;
            end;
            if b=0 then
              writeInfo;
          end else
            b:=0;
        End;
      end;
      k:=1;
      if eof(fil) then
        j:=0
      else
        blockRead(fil,buf,1);
      if b>0 then begin
        reco:=buf[1];
        show;
        writeinfo;
        b:=0;
        k:=2;
      end;
    end;
  end;
{  gettime(y,y2,y3,y4);
  writeln(y3:4,y4:4);}
  textattr:=blue;
  heb:='ÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ';
  Writeln(heb,heb,heb,heb);
  if count>0 then
    WriteLn(t,heb,heb,heb,heb);
  textattr:=cyan;
  p:=find;
  if c=#1 then begin
    write('"');
    if count>0 then
      write(t,'"');
  end else
    str(size,find);
  write(find,oo[c],' äåôòåú ì');
  If Count>0 Then begin
    writeln(t,find,oo[c],'Œäåôòåú',count);
    Write(Count);
    writeln(t,xxx,' ƒ’… ',names[x],'');
    if (c=#2) and (w>'') then
      writeln(t,'"',w,'ä÷èò äùååä áòøëå ìôñå÷éí äğ"ì äåà "');
  end Else
    Write('…€– €Œ');
  close(t);
  if count=0 then
    erase(t);
  textattr:=lightGray;
  writeln;
End.
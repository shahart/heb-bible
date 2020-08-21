{$A+,B-,d-,E-,F-,G+,I+,L-,N-,O-,P-,Q-,R-,S-,T-,V+,X+}
Uses crt;
Const
  engleter:string[28]=' TCDSVUZJYHFKNBXGPMERA,L;.OI';
  hebleter:string[28]=' €‚ƒ„…†‡ˆ‰‹Œ‘’”–—˜™šŠ“•';
  helper='!@#$%^&';
  Names:Array [1..39] Of String[13]=('š‰™€˜','š…™','€˜—‰…','˜ƒ','‰˜ƒ','’™…„‰','‰ˆ”…™','''€ Œ€…™',''' Œ€…™',
    '''€ ‰‹Œ',''' ‰‹Œ','„‰’™‰','„‰˜‰','Œ€—†‡‰','’™…„','Œ€…‰','‘…’','„‰ƒ…’','„…‰','„‹‰','…‡','—…—‡','„‰”–',
    '‰‚‡','„‰˜‹†','‰‹€Œ','‰Œ„š','‰Œ™','…‰€','‰˜‰™„ ˜‰™','š…˜','„‹‰€','šŒ„—','˜š‘€','Œ€‰ƒ','€˜†’','„‰‡',
    '''€ ‰‰„ ‰˜ƒ',''' ‰‰„ ‰˜ƒ');
  ZZ:Array [1..40] Of Integer=(1,2044,3712,4898,6614,8017,9033,10010,11295,12370,13653,14823,16550,18675,20560,20807,20905,
    21110,21140,21210,21350,21408,21480,21559,21617,21931,22015,24568,25485,26583,26722,26851,27048,27335,27628,28222,28633,
    29219,30431,31775);
  bookeng:array [1..39] of string[13]=('Genesis','Exodus','Leviticus','Numbers','Deuteronomy','Joshua','Judges','Samuel 1',
    'Samuel 2','Kings 1','Kings 2','Isaiah','Jeremiah','Ezekiel','Hosea','Joel','Amos','Obadiah','Jonah','Micha','Nachum',
    'Habakkuk','Zephaniah','Haggai','Zechariah','Malachi','Psalms','Proverbs','Job','Song of songs','Ruth','Lamentations',
    'Ecclesiastes','Esther','Daniel','Ezra','Nehemiah','Cronicles 1','Cronicles 2');
  words:array [1..39] of word=(20612,16713,11950,16408,14294,10031,9885,13265,11033,13140,12273,16922,21831,18730,2381,957,
    2042,291,688,1396,558,671,767,600,3126,876,19559,6915,8340,1250,1294,1542,2987,3045,5923,3754,5312,10745,13315);
Type
  String79=String[79];
  Rec=Record
    Perek,Pasuk:Byte;
    Txt:array [1..45] of byte;
  End;
Var
  m,i,q,PPsk,PPrk,Y,Par1,Par2:Byte;
  TextFile:File Of Rec;
  Found1,Found2:Boolean;
  FindStr2:Rec;
  FindStr:String[50];
  x,p,tot,EndFile,Now:Integer;
  Line:Array [1..2703] Of ^String79;
  key:string[10] absolute $40:$1e;
  bk:array [0..9] of integer;
  c:char;
  s:string[72];

procedure decrypt;
begin
  m:=1;
  for i:=1 to 72 do begin
    s[i]:=chr(128 or findstr2.txt[m] shr 3);                                    {11111000}
    s[i+1]:=chr(128 or findstr2.txt[m] and 7 shl 2 or findstr2.txt[m+1] shr 6); {00000111+11000000}
    s[i+2]:=chr(128 or findstr2.txt[m+1] and 62 shr 1);                                  {00111110}
    s[i+3]:=chr(128 or findstr2.txt[m+1] and 1 shl 4 or findstr2.txt[m+2] shr 4);        {00000001+11110000}
    s[i+4]:=chr(128 or findstr2.txt[m+2] and 15 shl 1 or findstr2.txt[m+3] shr 7);                {00001111+10000000}
    s[i+5]:=chr(128 or findstr2.txt[m+3] and 124 shr 2);                                                   {01111100}
    s[i+6]:=chr(128 or findstr2.txt[m+3] and 3 shl 3 or findstr2.txt[m+4] shr 5);                          {00000011+11100000}
    s[i+7]:=chr(128 or findstr2.txt[m+4] and 31);                                                                   {00011111}
    inc(m,5);
    inc(i,7);
  end;
  for i:=1 to 69 do
    if s[i]=#159 then
      s[i]:=#32;
end;

procedure wait;
begin
  readkey;
  if keyPressed then
    c:=readkey;
end;

Procedure PrintScr(Line1:Integer);
Begin
  textAttr:=lightblue;
  If Line1<1 Then
    Line1:=1
  else If Line1>EndFile-23 Then
    Line1:=EndFile-23;
  Now:=Line1;
  GotoXY(1,1);
  For Line1:=Now To Now+23 Do
    Write(' '+Line[Line1]^);
  textAttr:=white;
  Write('** ',Names[Y],' ** ',Now,' „˜…™ ** Info Find Prv Nxt 1-7:to memo. +shift:set memo');
  clreol;
  write(^m);
End;

Procedure StrInFile(I:Integer;bb:boolean);
Begin
  gotoxy(4,25);
  clreol;
  If FindStr>'' Then Begin
    For I:=I To EndFile Do begin
      par1:=Pos(FindStr,'  '+Line[I]^+#0);
      if par1>0 Then Begin
	printscr(i);
	gotoxy(par1-1,(i-endfile+23)*byte(i>endfile-23)+1);
	if (pos('  ',findstr)=1) and (par1=1) then
	  write(copy(findstr,2,length(findstr)-1))
	else
	  write(copy(findstr,1,length(findstr)-byte(findstr[length(findstr)]='³')));
	gotoxy(1,25);
        bk[9]:=now;
        write('*'#8);
	Exit;
      End else if bb then begin
	dec(i,2);
	if i<0 then
	  i:=endfile;
      end;
    end;
    textAttr:=red;
    write('€– €Œ'^m#7);
    wait;
  End;
  textattr:=White;
  Write(^m'** ',Names[Y],' ** ',Now,' „˜…™ ** Info Find Prv Nxt 1-7:to memo. +shift:set memo');
  clreol;
  write(^m);
End;

Begin
  If LastMode<2 Then
    TextMode(Co80);
  s[0]:=#71;
  CheckBreak:=False;
  CheckSnow:=False;
  Val(ParamStr(1),Y,EndFile);
  if endfile>1 then
    val(copy(paramstr(1),1,endfile-1),y,endfile);
  If (Y<1) Or (Y>39) Then Begin
    clrscr;
    textattr:=lightBlue;
    WriteLn('³ ‰…š‹':29,'³  ‰€‰':26,'³ „˜…š':20,^m^j,'ÀÄÄÄÄÄÄÄ':29,'³ ‰…™€˜':26,'ÀÄÄÄÄÄ':20);
    WriteLn('ÀÄÄÄÄÄÄÄÄ':55,^j^j^m,'³ š…Œ‰‚':29,^m^j,'ÀÄÄÄÄÄÄÄ':29,^j^m^j,'³  ‰€‰':55,^m^j,'³ ‰…˜‡€':55);
    WriteLn('ÀÄÄÄÄÄÄÄÄ':55,^j^j^m,'³ ˜™’ ‰˜š':55,^m^j,'ÀÄÄÄÄÄÄÄÄ':55);
    gotoxy(1,wherey-13);
    for tot:=27 to 39 do begin
      writeln(names[tot]:17,' ',tot);
      if tot in [29,34] then
	writeln;
    end;
    window(30,wherey-15,80,25);
    for tot:=6 to 26 do begin
      writeln(names[tot]:13,' ',tot);
      if tot in [11,14] then
	writeln;
    end;
    window(61,1,80,25);
    for tot:=1 to 5 do
      writeln(names[tot]:6,tot:2);
    window(1,17,80,25);
    textAttr:=lightred;
    findstr:='ÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ';
    WriteLn('Ú'+findstr+'¿':32,^m^j,'³ Š…–˜™ ˜”‘„ ''‘ š€ ™—„ ³':32,^m^j,'³  —…‘”„… —˜”„ š€ ,š…€˜Œ  ³':32);
    writeln('³   Enter-… (‰Œ…‰–”…€)   ³':32,^m^j,'À'+findstr+'Ù':32);
    textAttr:=lightgreen;
    WriteLn(^j'ş Œˆ ˜‡™ (c) ''98':19);
    key:=' R e a d  ';
    mem[$40:$1c]:=$2a;
    mem[$40:$1a]:=$1e;
    exit;
  End;
  EndFile:=0;
  PPrk:=32;
  fillChar(bk,20,0);
  PPsk:=17;
  tot:=-17;
  Val(ParamStr(2),q,Now);
  if now>1 then
    val(copy(paramstr(2),1,now-1),q,now);
  Val(ParamStr(3),Par2,Now);
  if now>1 then
    val(copy(paramstr(3),1,now-1),par2,now);
  Assign(TextFile,'Bible.Txt');
  {$i-} Reset(TextFile); {$i+}
  if ioresult<>0 then begin
    textattr:=cyan;
    write(^j'ş Bible.Txt š‡‰š” š…’ˆ'#7);
    textAttr:=7;
    writeln;
    exit;
  end;
  textaTtr:=bRown;
  Writeln(^j'š„ €€ ...');
  Seek(TextFile,ZZ[Y]-1);
  Read(TextFile,FindStr2);
  Found1:=q=1;
  Found2:=found1 and (par2=1);
  Repeat
    Inc(EndFile);
    New(Line[EndFile]);
    If FindStr2.Perek<>PPrk Then Begin
      Line[EndFile]^:='                                                                        ³      ';
      Inc(EndFile);
      New(Line[EndFile]);
    End;
    decrypt;
    Line[EndFile]^:=s+' ³';
    If PPrk<>FindStr2.Perek Then Begin
      Str(FindStr2.Perek-31:3,findstr);
      Line[EndFile]^:=Line[EndFile]^+findstr;
      inc(tot,ppsk-31);
    End Else
      Line[EndFile]^:=Line[EndFile]^+'   ';
    if ppsk<>findstr2.pasuk then begin
      Str(FindStr2.Pasuk-31:3,findstr);
      Line[EndFile]^:=Line[EndFile]^+findstr;
    End Else
      Line[EndFile]^:=Line[EndFile]^+'   ';
    PPrk:=FindStr2.Perek;
    PPsk:=FindStr2.Pasuk;
    Read(TextFile,FindStr2);
    If FindStr2.Perek=q+31 Then begin
      Found1:=True;
      If FindStr2.Pasuk=Par2+31 Then
	Found2:=True;
    end;
  Until FilePos(TextFile)=ZZ[Y+1];
  if y=5 then
    line[1330]^[70]:=' ';                   {********}
  line[1]^[76]:='1';
  If Y=39 Then Begin
    Inc(EndFile);
    New(Line[EndFile]);
    decrypt;
    Line[EndFile]^:=s+' ³      ';
  End;
  If Not Found1 And (q>0) Or Not Found2 And (Par2>0) Then Begin
    textAttr:=red;
    Write('ş €– €Œ'#7^m);
    wait;
  End;
  val(paramstr(5),x,p);
  if (paramstr(4)='à') and not(x in [0,13,24,34,44,55]) then begin {š…˜ˆ”„„ “…‘ š˜‰™Œ ParamStr(5)}
    findstr:=' '+paramstr(6)+' ';
    strinfile(1,false);
    findstr:=' '+paramstr(7)+#0;
    if findstr<>' 1'#0 then
      strinfile(now,false);
    bk[8]:=now;
    line[now]^[73]:='µ';
  end;
  if Found1 Then Begin
    str(q,findstr);
    FindStr:=findstr+' ';
    StrInFile(1,false);
    If (par2>1) and Found2 Then Begin
      str(par2,findstr);
      findstr:=' '+findstr+#0;
      StrInFile(Now+byte(par2=q),false);
    End;
    bk[0]:=now;
    line[now]^[73]:='µ';
    if (paramstr(4)='à') and (x in [13,24,34,44,55]) then begin
      if endfile-bk[0]>23 then
        bk[8]:=endfile
      else begin
        bk[8]:=endfile-4-byte(x=24)+byte(x=44)-16*byte(x=55);
        line[endfile]^[73]:='µ';
        line[endfile-23]^[73]:='³';
      end;
      line[bk[8]]^[73]:='µ';
    end;
    printscr(now);
  End else
    printScr(1);
  FindStr:='';
  bk[9]:=0;
  Repeat
    c:=upcasE(readkey);
    Case c Of
    '0'..'9':if bk[ord(c)-48]<>0 then
               printscr(bk[ord(c)-48]);
    'P','N':StrInFile(now-ord(c)+79,c='P');
    #27:begin
          textattr:=lightGray;
	  clrscr;
	  exit;
	end;
    'I':Begin
	  textattr:=lightmagenta;
          gotoxy(1,2);
	  writeln('  ÕÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍ¸'^m^j'  ³',bookeng[y]:13,' :š‰Œ‚€ ™„        ³');
	  writeln('  ³',tot+ppsk-14:13,' :‰—…‘”„ ˜”‘       ³'^m^j'  ³',words[y]:13,' :‰Œ„ ˜”‘         ³');
	  write('  ÔÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍ¾');
	  gotoxy(1,25);
          wait;
	  printScr(now);
	end;
    #0:Case ReadKey Of
	 #71:PrintScr(1);
	 #73:PrintScr(Now-23);
	 #72:PrintScr(Now-1);
	 #79:PrintScr(EndFile);
	 #81:PrintScr(Now+23);
	 #80:PrintScr(Now+1);
       End;
    'F':Begin
	  Window(4,25,80,25);
	  textAttr:=yellow;
	  Write(^j'€–: ');
	  textAttr:=7;
          findStr:='';
          q:=wherex;
          repeat
            repeat
              c:=upcase(readkey);
              if c=#0 then begin
                c:=readkey;
                c:=#0;
              end;
            until c>#0;
            if c=#27 then begin
               findStr:='';
               clreol;
            end else if length(findStr)<48 then
              if pos(c,engleter)>0 then
                findStr:=hebleter[pos(c,engleter)]+findStr
              else if c in ['0'..'9'] then
                findStr:=findStr+c;
            write(findStr);
            gotoxy(q,wherey);
            if c=#8 then begin
              if findStr[length(findStr)] in ['0'..'9'] then
                dec(findStr[0])
              else
                delete(findStr,1,1);
              write(findStr,' ');
              gotoxy(q,wherey);
            end;
          until c=#13;
          writeln;
	  textattr:=cyan;
          if findstr[length(findstr)] in ['0'..'9'] then begin
            if findstr[1]=' ' then
              findstr:=findstr+' ';
	    while findstr[1]=' ' do
	      delete(findstr,1,1);
          end else begin
	    while pos('  ',copy(findstr,2,length(findstr)-2))>0 do
	      delete(findstr,pos('  ',copy(findstr,2,length(findstr)-2))+1,1);
	    if copy(findstr,length(findstr)-1,2)='  ' then
	      findstr[length(findstr)]:='³';
          end;
          Window(1,1,80,25);
          if (findstr>'') and (findstr[1] in ['0'..'9']) and (pos(' ',findstr)=0) then begin
            findstr:=' '+findstr+#0;
            strinfile(now,false);
	  end else if (findstr>'') and (findstr[1] in ['0'..'9']) then begin
            if length(findstr)<4 then
              findstr:=' '+findstr;
	    strinfile(1,false);
          end else
	    StrInFile(Now,false);
	End;
    else if pos(c,helper)>0 then
      bk[pos(c,helper)]:=now;
    End;
  Until false;
End.
import os
import sys
import matplotlib.pyplot as plt
import matplotlib.patches as patches
from reportlab.lib.pagesizes import letter, landscape
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image, PageBreak, KeepTogether, HRFlowable
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch
from reportlab.pdfgen import canvas

# ==============================================================================
# 1. GENERATE VISUAL ER DIAGRAM (Zero-Crossing 4x3 Grid, Standard ER Model)
# ==============================================================================
def generate_er_diagram_image(img_output_path):
    fig, ax = plt.subplots(figsize=(25, 16.5), dpi=300)
    ax.set_xlim(0, 25)
    ax.set_ylim(0, 16.5)
    ax.axis('off')

    # Canvas Background
    fig.patch.set_facecolor('#F8FAFC')
    ax.set_facecolor('#F8FAFC')

    # Color Palette
    HEADER_BG = '#1E3A8A'      # Deep Navy Blue
    BODY_BG = '#FFFFFF'        # Crisp Pure White
    BORDER_COLOR = '#3B82F6'   # Royal Blue Border
    TEXT_MAIN = '#0F172A'      # Dark Slate for Attribute Names
    TEXT_MUTED = '#64748B'     # Slate for Types
    PK_COLOR = '#B45309'       # Amber / Bronze for PK
    FK_COLOR = '#0284C7'       # Cyan / Sky Blue for FK
    LINE_COLOR = '#334155'     # Dark Slate Grey for Lines
    DIAMOND_BG = '#EEF2FF'     # Soft Indigo Tint
    DIAMOND_BORDER = '#4F46E5' # Indigo Border
    CARD_COLOR = '#DC2626'     # Vibrant Red for Cardinality

    # 4 Columns x 3 Rows Zero-Crossing Grid Layout
    # Col 1: x = 0.8   (aircraft_model, seat, baggage)
    # Col 2: x = 6.9   (airline, aircraft, ticket)
    # Col 3: x = 13.0  (employee, flight, booking)
    # Col 4: x = 19.1  (airport, passenger, payment)
    # Row 1 (Top):    y = 10.4
    # Row 2 (Middle): y = 5.4
    # Row 3 (Bottom): y = 0.65

    entities = {
        # --- ROW 1 (TOP) ---
        'aircraft_model': {
            'x': 0.8, 'y': 10.5, 'w': 4.8, 'h': 3.5,
            'attrs': [
                ('model_id', 'INT', 'PK'),
                ('manufacturer', 'VARCHAR(50)', 'NOT NULL'),
                ('model_name', 'VARCHAR(50)', 'NOT NULL'),
                ('range_km', 'INT', 'CHECK > 0')
            ]
        },
        'airline': {
            'x': 6.9, 'y': 10.5, 'w': 4.8, 'h': 3.5,
            'attrs': [
                ('airline_id', 'INT', 'PK'),
                ('name', 'VARCHAR(100)', 'UQ, NOT NULL'),
                ('iata_code', 'CHAR(2)', 'UQ, NOT NULL'),
                ('country', 'VARCHAR(60)', 'NOT NULL')
            ]
        },
        'employee': {
            'x': 13.0, 'y': 10.0, 'w': 5.0, 'h': 4.3,
            'attrs': [
                ('employee_id', 'INT', 'PK'),
                ('airline_id', 'INT', 'FK'),
                ('base_airport_id', 'INT', 'FK (NULL)'),
                ('manager_id', 'INT', 'FK (NULL)'),
                ('first_name', 'VARCHAR(50)', 'NOT NULL'),
                ('last_name', 'VARCHAR(50)', 'NOT NULL'),
                ('email', 'VARCHAR(100)', 'UQ, NOT NULL'),
                ('designation', 'VARCHAR(20)', 'CHECK IN (...)'),
                ('hire_date', 'DATE', 'NOT NULL'),
                ('salary', 'DECIMAL(10,2)', 'CHECK > 0')
            ]
        },
        'airport': {
            'x': 19.1, 'y': 10.5, 'w': 4.8, 'h': 3.7,
            'attrs': [
                ('airport_id', 'INT', 'PK'),
                ('iata_code', 'CHAR(3)', 'UQ, NOT NULL'),
                ('name', 'VARCHAR(120)', 'NOT NULL'),
                ('city', 'VARCHAR(60)', 'NOT NULL'),
                ('country', 'VARCHAR(60)', 'NOT NULL')
            ]
        },

        # --- ROW 2 (MIDDLE) ---
        'seat': {
            'x': 0.8, 'y': 5.6, 'w': 4.8, 'h': 3.6,
            'attrs': [
                ('seat_id', 'INT', 'PK'),
                ('aircraft_id', 'INT', 'FK'),
                ('seat_number', 'VARCHAR(4)', 'NOT NULL'),
                ('seat_class', 'VARCHAR(10)', 'CHECK ECON|BIZ')
            ]
        },
        'aircraft': {
            'x': 6.9, 'y': 5.3, 'w': 4.8, 'h': 4.1,
            'attrs': [
                ('aircraft_id', 'INT', 'PK'),
                ('airline_id', 'INT', 'FK'),
                ('model_id', 'INT', 'FK'),
                ('registration_number', 'VARCHAR(15)', 'UQ, NOT NULL'),
                ('manufacture_year', 'SMALLINT', 'CHECK 1990-2035'),
                ('status', 'VARCHAR(15)', 'CHECK ACTIVE|...')
            ]
        },
        'flight': {
            'x': 13.0, 'y': 4.8, 'w': 5.0, 'h': 4.5,
            'attrs': [
                ('flight_id', 'INT', 'PK'),
                ('flight_number', 'VARCHAR(8)', 'NOT NULL'),
                ('aircraft_id', 'INT', 'FK'),
                ('departure_airport_id', 'INT', 'FK'),
                ('arrival_airport_id', 'INT', 'FK'),
                ('departure_time', 'DATETIME', 'NOT NULL'),
                ('arrival_time', 'DATETIME', 'NOT NULL'),
                ('base_price', 'DECIMAL(10,2)', 'CHECK > 0'),
                ('status', 'VARCHAR(12)', 'CHECK IN (...)')
            ]
        },
        'passenger': {
            'x': 19.1, 'y': 4.8, 'w': 4.8, 'h': 4.5,
            'attrs': [
                ('passenger_id', 'INT', 'PK'),
                ('first_name', 'VARCHAR(50)', 'NOT NULL'),
                ('last_name', 'VARCHAR(50)', 'NOT NULL'),
                ('email', 'VARCHAR(100)', 'UQ, NOT NULL'),
                ('password_hash', 'VARCHAR(100)', 'NOT NULL'),
                ('phone', 'VARCHAR(15)', 'UQ, NOT NULL'),
                ('date_of_birth', 'DATE', 'NOT NULL'),
                ('gender', 'CHAR(1)', 'CHECK M|F|O'),
                ('nationality', 'VARCHAR(40)', 'DEFAULT Indian'),
                ('passport_number', 'VARCHAR(15)', 'UQ (NULL)')
            ]
        },

        # --- ROW 3 (BOTTOM) ---
        'baggage': {
            'x': 0.8, 'y': 0.65, 'w': 4.8, 'h': 3.7,
            'attrs': [
                ('baggage_id', 'INT', 'PK'),
                ('ticket_id', 'INT', 'FK'),
                ('baggage_type', 'VARCHAR(8)', 'CHECK CABIN|CHECKED'),
                ('weight_kg', 'DECIMAL(5,2)', 'CHECK > 0 & <= 50'),
                ('tag_number', 'VARCHAR(12)', 'UQ, NOT NULL')
            ]
        },
        'ticket': {
            'x': 6.9, 'y': 0.5, 'w': 4.8, 'h': 4.1,
            'attrs': [
                ('ticket_id', 'INT', 'PK'),
                ('ticket_number', 'VARCHAR(14)', 'UQ, NOT NULL'),
                ('booking_id', 'INT', 'FK'),
                ('flight_id', 'INT', 'FK'),
                ('seat_id', 'INT', 'FK (NULL)'),
                ('fare', 'DECIMAL(10,2)', 'CHECK > 0'),
                ('status', 'VARCHAR(10)', 'CHECK PENDING|...')
            ]
        },
        'booking': {
            'x': 13.0, 'y': 0.65, 'w': 5.0, 'h': 3.7,
            'attrs': [
                ('booking_id', 'INT', 'PK'),
                ('booking_reference', 'CHAR(6)', 'UQ, NOT NULL'),
                ('passenger_id', 'INT', 'FK'),
                ('booking_date', 'DATETIME', 'NOT NULL'),
                ('status', 'VARCHAR(10)', 'CHECK PENDING|...')
            ]
        },
        'payment': {
            'x': 19.1, 'y': 0.65, 'w': 4.8, 'h': 3.9,
            'attrs': [
                ('payment_id', 'INT', 'PK'),
                ('booking_id', 'INT', 'FK'),
                ('amount', 'DECIMAL(10,2)', 'CHECK > 0'),
                ('method', 'VARCHAR(15)', 'CHECK CARD|UPI|...'),
                ('status', 'VARCHAR(10)', 'CHECK SUCCESS|...'),
                ('transaction_ref', 'VARCHAR(30)', 'UQ, NOT NULL'),
                ('paid_at', 'DATETIME', 'NOT NULL')
            ]
        }
    }

    # Render Entities
    for name, data in entities.items():
        x, y, w, h = data['x'], data['y'], data['w'], data['h']

        # Subtle Drop Shadow
        shadow = patches.FancyBboxPatch((x + 0.08, y - 0.08), w, h,
                                       boxstyle="round,pad=0.04,rounding_size=0.15",
                                       fc='#CBD5E1', ec='none', zorder=2)
        ax.add_patch(shadow)

        # Entity Body Box
        body_box = patches.FancyBboxPatch((x, y), w, h,
                                          boxstyle="round,pad=0.04,rounding_size=0.15",
                                          fc=BODY_BG, ec=BORDER_COLOR, lw=1.8, zorder=3)
        ax.add_patch(body_box)

        # Header Box (Top portion)
        header_h = 0.62
        header_box = patches.FancyBboxPatch((x, y + h - header_h), w, header_h,
                                            boxstyle="round,pad=0.04,rounding_size=0.15",
                                            fc=HEADER_BG, ec=HEADER_BG, lw=1.2, zorder=4)
        ax.add_patch(header_box)
        rect_cover = patches.Rectangle((x, y + h - header_h), w, 0.2,
                                       fc=HEADER_BG, ec=HEADER_BG, zorder=4)
        ax.add_patch(rect_cover)

        # Entity Name
        ax.text(x + w / 2, y + h - header_h / 2, name.upper(),
                color='#FFFFFF', fontsize=11.5, fontweight='bold',
                ha='center', va='center', zorder=5)

        # Attributes
        attrs = data['attrs']
        avail_h = h - header_h - 0.2
        step = avail_h / (len(attrs) + 0.2)
        cur_y = y + h - header_h - step * 0.7

        for attr_name, attr_type, key_tag in attrs:
            if 'PK' in key_tag:
                prefix = "[PK]"
                color_p = PK_COLOR
                is_bold = True
            elif 'FK' in key_tag:
                prefix = "[FK]"
                color_p = FK_COLOR
                is_bold = False
            else:
                prefix = " • "
                color_p = TEXT_MUTED
                is_bold = False

            ax.text(x + 0.2, cur_y, prefix, color=color_p, fontsize=8.5,
                    fontweight='bold', va='center', zorder=5)
            ax.text(x + 0.85, cur_y, attr_name, color=TEXT_MAIN, fontsize=9.0,
                    fontweight='bold' if is_bold else 'normal', va='center', zorder=5)
            ax.text(x + w - 0.2, cur_y, attr_type, color=TEXT_MUTED, fontsize=8.0,
                    ha='right', va='center', zorder=5)

            cur_y -= step

    # Helper for Connection Points
    def get_pt(entity_name, side):
        d = entities[entity_name]
        if side == 'right': return (d['x'] + d['w'], d['y'] + d['h'] / 2)
        if side == 'left': return (d['x'], d['y'] + d['h'] / 2)
        if side == 'top': return (d['x'] + d['w'] / 2, d['y'] + d['h'])
        if side == 'bottom': return (d['x'] + d['w'] / 2, d['y'])
        if side == 'bottom_left': return (d['x'] + d['w'] * 0.25, d['y'])
        if side == 'bottom_right': return (d['x'] + d['w'] * 0.75, d['y'])
        if side == 'top_left': return (d['x'] + d['w'] * 0.25, d['y'] + d['h'])
        if side == 'top_right': return (d['x'] + d['w'] * 0.75, d['y'] + d['h'])
        return (d['x'] + d['w'] / 2, d['y'] + d['h'] / 2)

    # 13 Direct Relationships (All completely non-crossing)
    relationships = [
        # --- ROW 1 ASSOCIATIONS ---
        # 1. aircraft_model 1 : N aircraft
        ('aircraft_model', 'aircraft', 'specifies', '1', 'N', 'bottom', 'top_left'),
        # 2. airline 1 : N aircraft
        ('airline', 'aircraft', 'operates', '1', 'N', 'bottom', 'top'),
        # 3. airline 1 : N employee
        ('airline', 'employee', 'employs', '1', 'N', 'right', 'left'),
        # 4. airport 1 : N employee
        ('airport', 'employee', 'bases', '1', 'N', 'left', 'right'),

        # --- ROW 1 TO ROW 2 ASSOCIATIONS ---
        # 5. airport 1 : N flight
        ('airport', 'flight', 'routes', '1', 'N', 'bottom', 'top_right'),

        # --- ROW 2 ASSOCIATIONS ---
        # 6. aircraft 1 : N seat
        ('aircraft', 'seat', 'contains', '1', 'N', 'left', 'right'),
        # 7. aircraft 1 : N flight
        ('aircraft', 'flight', 'assigned', '1', 'N', 'right', 'left'),

        # --- ROW 2 TO ROW 3 ASSOCIATIONS ---
        # 8. seat 1 : 0..1 ticket
        ('seat', 'ticket', 'allocates', '1', '0..1', 'bottom', 'top_left'),
        # 9. flight 1 : N ticket
        ('flight', 'ticket', 'schedules', '1', 'N', 'bottom', 'top'),
        # 10. passenger 1 : N booking
        ('passenger', 'booking', 'makes', '1', 'N', 'bottom', 'top'),

        # --- ROW 3 ASSOCIATIONS ---
        # 11. ticket 1 : N baggage
        ('ticket', 'baggage', 'includes', '1', 'N', 'left', 'right'),
        # 12. booking 1 : N ticket
        ('booking', 'ticket', 'reserves', '1', 'N', 'left', 'right'),
        # 13. booking 1 : N payment
        ('booking', 'payment', 'paid via', '1', 'N', 'right', 'left'),
    ]

    for e1, e2, label, c1, c2, s1, s2 in relationships:
        p1 = get_pt(e1, s1)
        p2 = get_pt(e2, s2)

        # Direct connector line
        ax.plot([p1[0], p2[0]], [p1[1], p2[1]], color=LINE_COLOR, lw=1.5, linestyle='-', zorder=1)

        # Midpoint for relationship diamond
        mx = (p1[0] + p2[0]) / 2
        my = (p1[1] + p2[1]) / 2

        # Relationship diamond
        dw, dh = 1.15, 0.48
        diamond = patches.Polygon([
            (mx, my + dh/2),
            (mx + dw/2, my),
            (mx, my - dh/2),
            (mx - dw/2, my)
        ], closed=True, fc=DIAMOND_BG, ec=DIAMOND_BORDER, lw=1.3, zorder=6)
        ax.add_patch(diamond)

        # Label inside diamond
        ax.text(mx, my, label, color=DIAMOND_BORDER, fontsize=7.5, fontweight='bold',
                ha='center', va='center', zorder=7)

        # Cardinalities
        v1 = ((mx - p1[0]) * 0.16, (my - p1[1]) * 0.16)
        v2 = ((mx - p2[0]) * 0.16, (my - p2[1]) * 0.16)
        ax.text(p1[0] + v1[0], p1[1] + v1[1] + 0.12, c1, color=CARD_COLOR, fontsize=9.0,
                fontweight='bold', ha='center', va='center', zorder=7)
        ax.text(p2[0] + v2[0], p2[1] + v2[1] + 0.12, c2, color=CARD_COLOR, fontsize=9.0,
                fontweight='bold', ha='center', va='center', zorder=7)

    # 14. Recursive Self-Relationship: employee (manager_id) -> employee
    emp = entities['employee']
    loop_cx = emp['x'] + emp['w'] / 2
    loop_top = emp['y'] + emp['h']
    
    # Clean curved self-loop above employee box
    ax.annotate('', xy=(loop_cx + 0.8, loop_top), xytext=(loop_cx - 0.8, loop_top),
                arrowprops=dict(arrowstyle="->", connectionstyle="arc3,rad=-1.1",
                                color=LINE_COLOR, lw=1.5), zorder=1)
    
    ax.text(loop_cx, loop_top + 0.55, "manages (1 : N)", color=DIAMOND_BORDER, fontsize=7.5,
            fontweight='bold', ha='center', va='center',
            bbox=dict(boxstyle="round,pad=0.25", fc=DIAMOND_BG, ec=DIAMOND_BORDER, lw=1.1),
            zorder=6)

    # Top Title Banner
    title_box = patches.FancyBboxPatch((0.8, 15.2), 23.1, 0.85,
                                       boxstyle="round,pad=0.04,rounding_size=0.15",
                                       fc='#0F172A', ec='#334155', lw=1.5, zorder=3)
    ax.add_patch(title_box)
    ax.text(1.2, 15.62, "AIRLINE MANAGEMENT SYSTEM — STANDARD ENTITY-RELATIONSHIP (ER) DIAGRAM",
            color='#FFFFFF', fontsize=14.5, fontweight='bold', va='center', zorder=5)
    ax.text(23.6, 15.62, "Core Entities & Attributes • 3NF Normalized • Non-Enhanced ER",
            color='#94A3B8', fontsize=10.5, ha='right', va='center', zorder=5)

    # Bottom Legend
    leg_box = patches.FancyBboxPatch((0.8, 0.05), 23.1, 0.45,
                                     boxstyle="round,pad=0.03,rounding_size=0.08",
                                     fc='#FFFFFF', ec='#CBD5E1', lw=1.0, zorder=3)
    ax.add_patch(leg_box)
    ax.text(1.2, 0.27, "NOTATION LEGEND:", color='#0F172A', fontsize=9, fontweight='bold', va='center', zorder=5)
    ax.text(3.4, 0.27, "[PK] Primary Key (Unique Key)", color=PK_COLOR, fontsize=8.5, fontweight='bold', va='center', zorder=5)
    ax.text(7.6, 0.27, "[FK] Foreign Key (Referential Key)", color=FK_COLOR, fontsize=8.5, fontweight='bold', va='center', zorder=5)
    ax.text(12.2, 0.27, "◇ Relationship Diamond (Verb)", color=DIAMOND_BORDER, fontsize=8.5, fontweight='bold', va='center', zorder=5)
    ax.text(16.8, 0.27, "1 : N Relational Cardinality", color=CARD_COLOR, fontsize=8.5, fontweight='bold', va='center', zorder=5)
    ax.text(21.4, 0.27, "[UQ] Unique Constraint", color=TEXT_MUTED, fontsize=8.5, va='center', zorder=5)

    plt.tight_layout()
    plt.savefig(img_output_path, format='png', dpi=300, bbox_inches='tight', facecolor=fig.get_facecolor())
    plt.close()
    print(f"High-res zero-crossing ER diagram saved at {img_output_path}")

# ==============================================================================
# 2. NUMBERED CANVAS FOR HEADER & FOOTER
# ==============================================================================
class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super(NumberedCanvas, self).__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_decorations(num_pages)
            super(NumberedCanvas, self).showPage()
        super(NumberedCanvas, self).save()

    def draw_decorations(self, total_pages):
        self.saveState()
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748B"))
        w, h = self._pagesize

        # Running Header (pages > 1)
        if self._pageNumber > 1:
            self.drawString(36, h - 28, "Airline Management System — Standard ER Diagram & Attribute Specification")
            self.drawRightString(w - 36, h - 28, "Relational Schema 3NF")
            self.setStrokeColor(colors.HexColor("#CBD5E1"))
            self.setLineWidth(0.5)
            self.line(36, h - 32, w - 36, h - 32)

        # Running Footer
        self.setStrokeColor(colors.HexColor("#CBD5E1"))
        self.setLineWidth(0.5)
        self.line(36, 32, w - 36, 32)

        self.drawString(36, 20, "Database Specification • MySQL 8.0+ / InnoDB Engine • Standard ER Model")
        page_str = f"Page {self._pageNumber} of {total_pages}"
        self.drawRightString(w - 36, 20, page_str)
        self.restoreState()

# ==============================================================================
# 3. BUILD COMPLETE MULTI-PAGE PDF REPORT
# ==============================================================================
def build_er_pdf(pdf_output_path, img_diagram_path):
    # Landscape Letter: 11 x 8.5 inches (792 x 612 pt)
    doc = SimpleDocTemplate(
        pdf_output_path,
        pagesize=landscape(letter),
        leftMargin=36,
        rightMargin=36,
        topMargin=38,
        bottomMargin=38
    )

    styles = getSampleStyleSheet()

    # Typography styles
    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=22,
        leading=26,
        textColor=colors.HexColor('#0F172A'),
        spaceAfter=4
    )

    subtitle_style = ParagraphStyle(
        'DocSubTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=11,
        leading=15,
        textColor=colors.HexColor('#2563EB'),
        spaceAfter=10
    )

    sec_title_style = ParagraphStyle(
        'SecTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=13,
        leading=16,
        textColor=colors.HexColor('#1E3A8A'),
        spaceBefore=8,
        spaceAfter=6
    )

    entity_title_style = ParagraphStyle(
        'EntityTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10.5,
        leading=13,
        textColor=colors.HexColor('#0F172A'),
        spaceBefore=4,
        spaceAfter=3
    )

    body_style = ParagraphStyle(
        'Body_Custom',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.5,
        leading=11.5,
        textColor=colors.HexColor('#334155'),
        spaceAfter=4
    )

    badge_pk_style = ParagraphStyle(
        'PKBadge',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=7.5,
        leading=9.5,
        textColor=colors.HexColor('#B45309')
    )

    badge_fk_style = ParagraphStyle(
        'FKBadge',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=7.5,
        leading=9.5,
        textColor=colors.HexColor('#0284C7')
    )

    cell_style = ParagraphStyle(
        'CellRegular',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=7.5,
        leading=9.5,
        textColor=colors.HexColor('#0F172A')
    )

    cell_bold = ParagraphStyle(
        'CellBold',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=7.5,
        leading=9.5,
        textColor=colors.HexColor('#0F172A')
    )

    th_style = ParagraphStyle(
        'TableHeader',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.0,
        leading=10.0,
        textColor=colors.white
    )

    story = []

    # ==========================================================================
    # PAGE 1: TITLE, EXECUTIVE SUMMARY & ENTITY DIRECTORY
    # ==========================================================================
    story.append(Paragraph("Airline Management System", title_style))
    story.append(Paragraph("Standard Entity-Relationship (ER) Diagram & Attribute Specification", subtitle_style))

    # Architecture Scope Notice
    notice_text = (
        "<b>Model Scope & Classification:</b> This report presents the <b>Standard Entity-Relationship (ER) Model</b> "
        "for the Airline Management System database. In strict accordance with classical ER modeling principles (non-enhanced, "
        "omitting subclassing / specialization / generalization hierarchies), this model explicitly details the "
        "<b>12 fundamental entities</b>, their <b>attributes</b> (Primary Keys, Foreign Keys, data types, and check constraints), "
        "and their <b>direct relational associations</b> normalized in Third Normal Form (3NF)."
    )
    notice_table = Table([[Paragraph(notice_text, body_style)]], colWidths=[10.0 * inch])
    notice_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor('#F8FAFC')),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor('#CBD5E1')),
        ('PADDING', (0,0), (-1,-1), 6),
    ]))
    story.append(notice_table)
    story.append(Spacer(1, 8))

    # Relational Metrics Bar
    stats_data = [
        [
            Paragraph("<b>Total Entities:</b> 12", cell_style),
            Paragraph("<b>Direct Relationships:</b> 13", cell_style),
            Paragraph("<b>Recursive Hierarchies:</b> 1 (Employee)", cell_style),
            Paragraph("<b>RDBMS Engine:</b> MySQL 8.0+ (InnoDB / 3NF)", cell_style)
        ]
    ]
    stats_table = Table(stats_data, colWidths=[2.5*inch, 2.5*inch, 2.5*inch, 2.5*inch])
    stats_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor('#EEF2FF')),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor('#C7D2FE')),
        ('PADDING', (0,0), (-1,-1), 5),
        ('ALIGN', (0,0), (-1,-1), 'CENTER')
    ]))
    story.append(stats_table)
    story.append(Spacer(1, 10))

    # Quick Summary Table of All 12 Entities
    story.append(Paragraph("System Entity Directory at a Glance", sec_title_style))
    
    summary_headers = [
        Paragraph("#", th_style),
        Paragraph("Entity Name", th_style),
        Paragraph("Primary Key [PK]", th_style),
        Paragraph("Foreign Keys [FK]", th_style),
        Paragraph("Core Business Function", th_style)
    ]
    summary_rows = [
        summary_headers,
        [Paragraph("1", cell_style), Paragraph("<b>airline</b>", cell_bold), Paragraph("airline_id", badge_pk_style), Paragraph("None", cell_style), Paragraph("Commercial airline operating companies", cell_style)],
        [Paragraph("2", cell_style), Paragraph("<b>airport</b>", cell_bold), Paragraph("airport_id", badge_pk_style), Paragraph("None", cell_style), Paragraph("Commercial airport terminals and facilities", cell_style)],
        [Paragraph("3", cell_style), Paragraph("<b>aircraft_model</b>", cell_bold), Paragraph("model_id", badge_pk_style), Paragraph("None", cell_style), Paragraph("Standard aircraft specifications and models", cell_style)],
        [Paragraph("4", cell_style), Paragraph("<b>aircraft</b>", cell_bold), Paragraph("aircraft_id", badge_pk_style), Paragraph("airline_id, model_id", badge_fk_style), Paragraph("Physical fleet airframes and registrations", cell_style)],
        [Paragraph("5", cell_style), Paragraph("<b>seat</b>", cell_bold), Paragraph("seat_id", badge_pk_style), Paragraph("aircraft_id", badge_fk_style), Paragraph("Cabin seat positions and class tiers", cell_style)],
        [Paragraph("6", cell_style), Paragraph("<b>flight</b>", cell_bold), Paragraph("flight_id", badge_pk_style), Paragraph("aircraft_id, dep_airport_id, arr_airport_id", badge_fk_style), Paragraph("Scheduled flight sectors between airports", cell_style)],
        [Paragraph("7", cell_style), Paragraph("<b>passenger</b>", cell_bold), Paragraph("passenger_id", badge_pk_style), Paragraph("None", cell_style), Paragraph("Registered travelers and customer accounts", cell_style)],
        [Paragraph("8", cell_style), Paragraph("<b>booking</b>", cell_bold), Paragraph("booking_id", badge_pk_style), Paragraph("passenger_id", badge_fk_style), Paragraph("Flight reservations and PNR bookings", cell_style)],
        [Paragraph("9", cell_style), Paragraph("<b>ticket</b>", cell_bold), Paragraph("ticket_id", badge_pk_style), Paragraph("booking_id, flight_id, seat_id", badge_fk_style), Paragraph("Issued boarding coupons linking flight & seat", cell_style)],
        [Paragraph("10", cell_style), Paragraph("<b>payment</b>", cell_bold), Paragraph("payment_id", badge_pk_style), Paragraph("booking_id", badge_fk_style), Paragraph("Financial payment transactions and settlements", cell_style)],
        [Paragraph("11", cell_style), Paragraph("<b>baggage</b>", cell_bold), Paragraph("baggage_id", badge_pk_style), Paragraph("ticket_id", badge_fk_style), Paragraph("Checked and cabin baggage allowances", cell_style)],
        [Paragraph("12", cell_style), Paragraph("<b>employee</b>", cell_bold), Paragraph("employee_id", badge_pk_style), Paragraph("airline_id, base_airport_id, manager_id", badge_fk_style), Paragraph("Flight crew, ground crew, and managers", cell_style)]
    ]
    summary_table = Table(summary_rows, colWidths=[0.3*inch, 1.4*inch, 1.5*inch, 2.6*inch, 4.2*inch])
    summary_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#1E3A8A')),
        ('ALIGN', (0,0), (-1,-1), 'LEFT'),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.white, colors.HexColor('#F8FAFC')]),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor('#E2E8F0')),
        ('PADDING', (0,0), (-1,-1), 3.5),
    ]))
    story.append(summary_table)

    # PAGE BREAK TO DEDICATED VISUAL ER DIAGRAM PAGE
    story.append(PageBreak())

    # ==========================================================================
    # PAGE 2: FULL-PAGE HIGH-RESOLUTION GRAPHICAL ER DIAGRAM
    # ==========================================================================
    story.append(Paragraph("1. Graphical Entity-Relationship (ER) Diagram", sec_title_style))
    story.append(Paragraph(
        "Standard ER Diagram showcasing all 12 entities, their attribute lists with Primary Keys [PK] and Foreign Keys [FK], "
        "and direct relationship paths with cardinalities (1 : N, 1 : 1). Zero-crossing layout ensures optimal clarity.",
        body_style
    ))
    story.append(Spacer(1, 4))

    # Full landscape diagram (10.0 x 6.4 inch)
    img = Image(img_diagram_path, width=10.0*inch, height=6.4*inch)
    story.append(img)

    # PAGE BREAK TO DATA DICTIONARY
    story.append(PageBreak())

    # ==========================================================================
    # PAGES 3+: COMPREHENSIVE ENTITY & ATTRIBUTE CATALOG (DATA DICTIONARY)
    # ==========================================================================
    story.append(Paragraph("2. Comprehensive Entity and Attribute Catalog", sec_title_style))
    story.append(Paragraph(
        "Complete technical catalog detailing each entity's attributes, data types, constraints, nullability, and relational linkages.",
        body_style
    ))
    story.append(Spacer(1, 6))

    entity_catalog = [
        {
            'name': '1. airline',
            'desc': 'Represents commercial airlines operating flights in the network.',
            'rels': '• 1 : N with <b>aircraft</b> (owns/operates fleet)<br/>• 1 : N with <b>employee</b> (employs airline workforce)',
            'attrs': [
                ('airline_id', 'INT', 'PK, Auto Increment', 'Unique surrogate primary key for the airline'),
                ('name', 'VARCHAR(100)', 'UNIQUE, NOT NULL', 'Full registered commercial name (e.g., IndiGo, Air India)'),
                ('iata_code', 'CHAR(2)', 'UNIQUE, NOT NULL', 'Two-letter standard IATA airline code (e.g., 6E, AI)'),
                ('country', 'VARCHAR(60)', 'NOT NULL', 'Country of registration and headquarters')
            ]
        },
        {
            'name': '2. airport',
            'desc': 'Represents origin and destination airport facilities.',
            'rels': '• 1 : N with <b>flight</b> as departure_airport_id<br/>• 1 : N with <b>flight</b> as arrival_airport_id<br/>• 1 : N with <b>employee</b> as base_airport_id',
            'attrs': [
                ('airport_id', 'INT', 'PK, Auto Increment', 'Unique surrogate primary key for the airport'),
                ('iata_code', 'CHAR(3)', 'UNIQUE, NOT NULL', 'Standard three-letter IATA code (e.g., DEL, BOM, BLR)'),
                ('name', 'VARCHAR(120)', 'NOT NULL', 'Official airport name (e.g., Indira Gandhi International Airport)'),
                ('city', 'VARCHAR(60)', 'NOT NULL', 'City served by the airport'),
                ('country', 'VARCHAR(60)', 'NOT NULL', 'Country where airport is located')
            ]
        },
        {
            'name': '3. aircraft_model',
            'desc': 'Stores standardized aircraft manufacturing specifications and flight range.',
            'rels': '• 1 : N with <b>aircraft</b> (categorizes physical fleet aircraft into standardized models)',
            'attrs': [
                ('model_id', 'INT', 'PK, Auto Increment', 'Unique surrogate primary key for the aircraft model'),
                ('manufacturer', 'VARCHAR(50)', 'NOT NULL', 'Aircraft manufacturing company (e.g., Airbus, Boeing)'),
                ('model_name', 'VARCHAR(50)', 'NOT NULL', 'Aircraft model designation (e.g., A320neo, B737-800, B787-9)'),
                ('range_km', 'INT', 'NOT NULL, CHECK > 0', 'Maximum flight operational range in kilometers')
            ]
        },
        {
            'name': '4. aircraft',
            'desc': 'Represents individual physical aircraft airframes belonging to an airline fleet.',
            'rels': '• N : 1 with <b>airline</b> (owned/operated by airline)<br/>• N : 1 with <b>aircraft_model</b> (specifications reference)<br/>• 1 : N with <b>seat</b> (contains passenger cabin seats)<br/>• 1 : N with <b>flight</b> (assigned to scheduled flight sectors)',
            'attrs': [
                ('aircraft_id', 'INT', 'PK, Auto Increment', 'Unique surrogate primary key for the physical airframe'),
                ('airline_id', 'INT', 'FK -> airline', 'Operating airline owning this aircraft'),
                ('model_id', 'INT', 'FK -> aircraft_model', 'Aircraft model specifications reference'),
                ('registration_number', 'VARCHAR(15)', 'UNIQUE, NOT NULL', 'Civil aviation tail number (e.g., VT-IZD, VT-EXO)'),
                ('manufacture_year', 'SMALLINT', 'NOT NULL, CHECK 1990-2035', 'Year of aircraft manufacture and rollout'),
                ('status', 'VARCHAR(15)', 'DEFAULT ACTIVE, CHECK', 'Fleet operational state: ACTIVE, MAINTENANCE, or RETIRED')
            ]
        },
        {
            'name': '5. seat',
            'desc': 'Represents individual cabin seat coordinates configured inside an aircraft.',
            'rels': '• N : 1 with <b>aircraft</b> (configured inside physical aircraft)<br/>• 1 : 0..1 with <b>ticket</b> (allocated to ticket for a scheduled flight sector)',
            'attrs': [
                ('seat_id', 'INT', 'PK, Auto Increment', 'Unique surrogate primary key for the seat configuration'),
                ('aircraft_id', 'INT', 'FK -> aircraft', 'Aircraft airframe containing this seat'),
                ('seat_number', 'VARCHAR(4)', 'NOT NULL', 'Seat coordinate inside cabin (e.g., 01A, 03B, 12F)'),
                ('seat_class', 'VARCHAR(10)', 'NOT NULL, CHECK', 'Cabin service tier: ECONOMY or BUSINESS')
            ]
        },
        {
            'name': '6. flight',
            'desc': 'Represents scheduled commercial flight operations connecting two airports.',
            'rels': '• N : 1 with <b>aircraft</b> (airframe deployed)<br/>• N : 1 with <b>airport</b> (departure origin)<br/>• N : 1 with <b>airport</b> (arrival destination)<br/>• 1 : N with <b>ticket</b> (passenger tickets issued for flight sector)',
            'attrs': [
                ('flight_id', 'INT', 'PK, Auto Increment', 'Unique surrogate primary key for the scheduled flight sector'),
                ('flight_number', 'VARCHAR(8)', 'NOT NULL', 'Flight code identifier (e.g., 6E-2041, AI-101)'),
                ('aircraft_id', 'INT', 'FK -> aircraft', 'Physical aircraft deployed on this flight'),
                ('departure_airport_id', 'INT', 'FK -> airport', 'Origin departure airport facility'),
                ('arrival_airport_id', 'INT', 'FK -> airport', 'Destination arrival airport facility'),
                ('departure_time', 'DATETIME', 'NOT NULL', 'Scheduled date and time of departure'),
                ('arrival_time', 'DATETIME', 'NOT NULL, CHECK > dep', 'Scheduled date and time of arrival'),
                ('base_price', 'DECIMAL(10,2)', 'NOT NULL, CHECK > 0', 'Base passenger airfare in local currency (INR)'),
                ('status', 'VARCHAR(12)', 'DEFAULT SCHEDULED, CHECK', 'State: SCHEDULED, DELAYED, CANCELLED, DEPARTED, ARRIVED')
            ]
        },
        {
            'name': '7. passenger',
            'desc': 'Stores customer identity profiles, travel documentation, and authentication data.',
            'rels': '• 1 : N with <b>booking</b> (initiates passenger ticket reservations)',
            'attrs': [
                ('passenger_id', 'INT', 'PK, Auto Increment', 'Unique surrogate primary key for the passenger'),
                ('first_name', 'VARCHAR(50)', 'NOT NULL', 'Passenger given first name'),
                ('last_name', 'VARCHAR(50)', 'NOT NULL', 'Passenger family / surname'),
                ('email', 'VARCHAR(100)', 'UNIQUE, NOT NULL', 'Registered login and notification email address'),
                ('password_hash', 'VARCHAR(100)', 'NOT NULL', 'Cryptographic hash for authenticated portal access'),
                ('phone', 'VARCHAR(15)', 'UNIQUE, NOT NULL', 'Primary contact telephone number'),
                ('date_of_birth', 'DATE', 'NOT NULL', 'Date of birth for age verification and fare tiering'),
                ('gender', 'CHAR(1)', 'CHECK M, F, O', 'Gender marker: M (Male), F (Female), O (Other)'),
                ('nationality', 'VARCHAR(40)', 'DEFAULT Indian, NOT NULL', 'Country of legal citizenship'),
                ('passport_number', 'VARCHAR(15)', 'UNIQUE, NULLABLE', 'International travel passport credential number')
            ]
        },
        {
            'name': '8. booking',
            'desc': 'Represents passenger reservation transactions and booking PNR headers.',
            'rels': '• N : 1 with <b>passenger</b> (made by registered customer)<br/>• 1 : N with <b>ticket</b> (contains individual passenger flight tickets)<br/>• 1 : N with <b>payment</b> (settled through financial payment transactions)',
            'attrs': [
                ('booking_id', 'INT', 'PK, Auto Increment', 'Unique surrogate primary key for the booking reservation'),
                ('booking_reference', 'CHAR(6)', 'UNIQUE, NOT NULL', '6-character alphanumeric PNR locator (e.g., PNR101, PNR102)'),
                ('passenger_id', 'INT', 'FK -> passenger', 'Registered passenger who created the booking'),
                ('booking_date', 'DATETIME', 'DEFAULT CURRENT_TIMESTAMP', 'Timestamp when booking reservation was submitted'),
                ('status', 'VARCHAR(10)', 'DEFAULT PENDING, CHECK', 'Reservation lifecycle state: PENDING, CONFIRMED, CANCELLED')
            ]
        },
        {
            'name': '9. ticket',
            'desc': 'Represents issued passenger flight coupons linking booking, flight, and allocated seat.',
            'rels': '• N : 1 with <b>booking</b> (line item in passenger reservation)<br/>• N : 1 with <b>flight</b> (scheduled flight sector reserved)<br/>• N : 1 with <b>seat</b> (allocated cabin seat; set NULL on cancellation)<br/>• 1 : N with <b>baggage</b> (associated registered baggage items)',
            'attrs': [
                ('ticket_id', 'INT', 'PK, Auto Increment', 'Unique surrogate primary key for the ticket'),
                ('ticket_number', 'VARCHAR(14)', 'UNIQUE, NOT NULL', 'Standardized 14-character e-ticket serial number (e.g., ETKT-2026-0001)'),
                ('booking_id', 'INT', 'FK -> booking', 'Associated parent booking reservation'),
                ('flight_id', 'INT', 'FK -> flight', 'Reserved scheduled flight sector'),
                ('seat_id', 'INT', 'FK -> seat, NULLABLE', 'Allocated aircraft seat (freed to NULL if ticket cancelled)'),
                ('fare', 'DECIMAL(10,2)', 'NOT NULL, CHECK > 0', 'Final charged passenger fare for this sector'),
                ('status', 'VARCHAR(10)', 'DEFAULT PENDING, CHECK', 'Coupon state: PENDING, ISSUED, or CANCELLED')
            ]
        },
        {
            'name': '10. payment',
            'desc': 'Records payment settlements and banking gateway transaction references.',
            'rels': '• N : 1 with <b>booking</b> (settles financial balance for reservation booking)',
            'attrs': [
                ('payment_id', 'INT', 'PK, Auto Increment', 'Unique surrogate primary key for the payment record'),
                ('booking_id', 'INT', 'FK -> booking', 'Associated reservation booking reference'),
                ('amount', 'DECIMAL(10,2)', 'NOT NULL, CHECK > 0', 'Monetary settlement amount in INR'),
                ('method', 'VARCHAR(15)', 'NOT NULL, CHECK', 'Payment channel: CARD, UPI, NET_BANKING, WALLET'),
                ('status', 'VARCHAR(10)', 'NOT NULL, CHECK', 'Transaction status: SUCCESS, FAILED, REFUNDED'),
                ('transaction_ref', 'VARCHAR(30)', 'UNIQUE, NOT NULL', 'Unique banking gateway transaction reference number'),
                ('paid_at', 'DATETIME', 'DEFAULT CURRENT_TIMESTAMP', 'Timestamp of financial transaction processing')
            ]
        },
        {
            'name': '11. baggage',
            'desc': 'Tracks checked and cabin baggage registered to specific passenger tickets.',
            'rels': '• N : 1 with <b>ticket</b> (associated with passenger flight ticket)',
            'attrs': [
                ('baggage_id', 'INT', 'PK, Auto Increment', 'Unique surrogate primary key for the baggage piece'),
                ('ticket_id', 'INT', 'FK -> ticket', 'Associated passenger flight ticket reference'),
                ('baggage_type', 'VARCHAR(8)', 'NOT NULL, CHECK', 'Baggage classification: CABIN or CHECKED'),
                ('weight_kg', 'DECIMAL(5,2)', 'NOT NULL, CHECK > 0 & <= 50', 'Weight of baggage piece in kilograms (up to 50 kg)'),
                ('tag_number', 'VARCHAR(12)', 'UNIQUE, NOT NULL', 'Standard barcode baggage tag identification number')
            ]
        },
        {
            'name': '12. employee',
            'desc': 'Represents airline personnel, flight crews, ground staff, and hierarchical managers.',
            'rels': '• N : 1 with <b>airline</b> (employed by airline)<br/>• N : 1 with <b>airport</b> (stationed at base airport)<br/>• N : 1 with <b>employee</b> (reports to manager; self-referencing hierarchy)',
            'attrs': [
                ('employee_id', 'INT', 'PK, Auto Increment', 'Unique surrogate primary key for the employee'),
                ('airline_id', 'INT', 'FK -> airline', 'Employer airline company reference'),
                ('base_airport_id', 'INT', 'FK -> airport, NULLABLE', 'Assigned base operating hub airport'),
                ('manager_id', 'INT', 'FK -> employee, NULLABLE', 'Reporting supervisor / manager employee identifier'),
                ('first_name', 'VARCHAR(50)', 'NOT NULL', 'Employee first given name'),
                ('last_name', 'VARCHAR(50)', 'NOT NULL', 'Employee family / surname'),
                ('email', 'VARCHAR(100)', 'UNIQUE, NOT NULL', 'Corporate communication email address'),
                ('designation', 'VARCHAR(20)', 'NOT NULL, CHECK', 'Role: PILOT, CO_PILOT, CABIN_CREW, GROUND_STAFF, ENGINEER, MANAGER'),
                ('hire_date', 'DATE', 'NOT NULL', 'Official joining and employment start date'),
                ('salary', 'DECIMAL(10,2)', 'NOT NULL, CHECK > 0', 'Monthly compensation remuneration in INR')
            ]
        }
    ]

    for idx, entity in enumerate(entity_catalog):
        flow = []

        # Entity Header
        flow.append(Paragraph(f"<b>Entity: {entity['name'].upper()}</b> — <i>{entity['desc']}</i>", entity_title_style))
        flow.append(Paragraph(f"<b>Relationships:</b> {entity['rels']}", body_style))
        flow.append(Spacer(1, 2))

        # Table rows
        t_rows = [
            [
                Paragraph("Attribute Name", th_style),
                Paragraph("Data Type", th_style),
                Paragraph("Key / Constraint", th_style),
                Paragraph("Description & Rules", th_style)
            ]
        ]

        for attr_name, attr_type, constraints, desc in entity['attrs']:
            if 'PK' in constraints:
                badge = Paragraph(f"<b>[PK]</b> {constraints.replace('PK, ', '').replace('PK', '')}", badge_pk_style)
                name_cell = Paragraph(f"<b><u>{attr_name}</u></b>", cell_bold)
            elif 'FK' in constraints:
                badge = Paragraph(f"<b>[FK]</b> {constraints.replace('FK -> ', 'Ref: ')}", badge_fk_style)
                name_cell = Paragraph(f"<b>{attr_name}</b>", cell_bold)
            else:
                badge = Paragraph(constraints, cell_style)
                name_cell = Paragraph(attr_name, cell_style)

            type_cell = Paragraph(attr_type, cell_style)
            desc_cell = Paragraph(desc, cell_style)

            t_rows.append([name_cell, type_cell, badge, desc_cell])

        attr_table = Table(t_rows, colWidths=[2.2 * inch, 1.6 * inch, 2.2 * inch, 4.0 * inch])
        attr_table.setStyle(TableStyle([
            ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#1E3A8A')),
            ('ALIGN', (0,0), (-1,-1), 'LEFT'),
            ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
            ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.white, colors.HexColor('#F8FAFC')]),
            ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor('#E2E8F0')),
            ('PADDING', (0,0), (-1,-1), 3.5),
        ]))

        flow.append(attr_table)
        flow.append(Spacer(1, 8))

        story.append(KeepTogether(flow))
        # Add Page Break after every 2-3 entities to keep pages clean and organized
        if idx in [1, 3, 5, 7, 9]:
            story.append(PageBreak())

    # Build Document
    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"PDF successfully built at {pdf_output_path}")

if __name__ == '__main__':
    base_dir = r"d:\Airlines\Airlines"
    img_path = os.path.join(base_dir, "er_diagram.png")
    pdf_path = os.path.join(base_dir, "Airline_ER_Diagram.pdf")

    print("Step 1: Generating High-Resolution ER Diagram Image...")
    generate_er_diagram_image(img_path)

    print("Step 2: Generating Standard ER Model PDF Report...")
    build_er_pdf(pdf_path, img_path)

    print("Finished successfully!")

package com.rivigo.riconet.core.dto;


import com.rivigo.zoom.common.model.UnConnectedBoxDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by hitesh on 8/31/16.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UnConnectedBoxDetailDTO {

    private Long id;
    private Long depsId;
    private Long clientId;
    private String client;
    private String inboundLocationCode;
    private Long originId;
    private String origin;
    private String destination;
    private Long destinationId;
    private String cnote;
    private String barcodeDigit;
    private String newBarcode;
    private String selectedBarcode;
    private String clientBarcode;
    private DateTime reportedTime;
    private String reportedByName;
    private String inchargeName;
    private String invoice;
    private String remarks;
    private String issueResolvedBy;
    private DateTime issueResolvedOn;
    private List<DEPSDocumentDTO> depsDocumentDTOList;

    public UnConnectedBoxDetailDTO(UnConnectedBoxDetail unConnectedBoxDetail,
                                   List<DEPSDocumentDTO> depsDocumentDTOList) {
        this.id = unConnectedBoxDetail.getId();
        this.depsId = unConnectedBoxDetail.getDepsId();
        this.clientId = unConnectedBoxDetail.getClientId();
        this.originId = unConnectedBoxDetail.getOriginId();
        this.destinationId = unConnectedBoxDetail.getDestinationId();
        this.cnote = unConnectedBoxDetail.getCNote();
        this.barcodeDigit = unConnectedBoxDetail.getBarcodeDigit();
        this.selectedBarcode = unConnectedBoxDetail.getSelectedBarcode();
        this.clientBarcode = unConnectedBoxDetail.getClientBarcode();
        this.invoice = unConnectedBoxDetail.getInvoice();
        this.remarks = unConnectedBoxDetail.getRemarks();
        this.depsDocumentDTOList = depsDocumentDTOList;
    }

    public UnConnectedBoxDetailDTO(UnConnectedBoxDetail unConnectedBoxDetail, List<DEPSDocumentDTO> depsDocumentDTOList,
                                   String inboundLocationCode, String issueResolvedBy, DateTime issueResolvedOn){
        this(unConnectedBoxDetail,depsDocumentDTOList);
        this.inboundLocationCode = inboundLocationCode;
        this.issueResolvedBy = issueResolvedBy;
        this.issueResolvedOn = issueResolvedOn;
    }
}

